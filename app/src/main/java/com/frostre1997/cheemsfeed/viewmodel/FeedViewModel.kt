package com.frostre1997.cheemsfeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.frostre1997.cheemsfeed.auth.RedditAuthManager
import com.frostre1997.cheemsfeed.model.PostData
import com.frostre1997.cheemsfeed.network.RedditApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortMode { HOT, NEW, TOP }

data class FeedUiState(
    val posts: List<PostData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class FeedViewModel(
    private val oauthApi: RedditApi,
    private val publicApi: RedditApi
) : ViewModel() {

    class Factory(
        private val oauthApi: RedditApi,
        private val publicApi: RedditApi
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedViewModel(oauthApi, publicApi) as T
        }
    }

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState

    private var sortMode = SortMode.HOT
    private var currentSubreddit = "all"
    private var after: String? = null
    private var authManager: RedditAuthManager? = null

    fun setAuthManager(manager: RedditAuthManager) {
        authManager = manager
        fetchPosts()
    }

    fun setSortMode(mode: SortMode) {
        sortMode = mode
        after = null
        fetchPosts()
    }

    fun setSubreddit(subreddit: String) {
        currentSubreddit = subreddit
        after = null
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = if (authManager?.isLoggedIn() == true) {
                    val token = authManager?.getValidAccessToken()
                    if (token != null) {
                        fetchAuthenticated(token)
                    } else {
                        fetchPublic()
                    }
                } else {
                    fetchPublic()
                }
                val posts = response.data.children.map { it.data }
                after = response.data.after
                _uiState.value = _uiState.value.copy(
                    posts = posts,
                    isLoading = false,
                    isLoggedIn = authManager?.isLoggedIn() == true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load posts"
                )
            }
        }
    }

    fun logout() {
        authManager?.logout()
        _uiState.value = _uiState.value.copy(isLoggedIn = false)
        fetchPosts()
    }

    private suspend fun fetchAuthenticated(token: String) = when (sortMode) {
        SortMode.HOT -> oauthApi.getHotPosts(currentSubreddit, token = "Bearer $token")
        SortMode.NEW -> oauthApi.getNewPosts(currentSubreddit, token = "Bearer $token")
        SortMode.TOP -> oauthApi.getTopPosts(currentSubreddit, token = "Bearer $token")
    }

    private suspend fun fetchPublic() = when (sortMode) {
        SortMode.HOT -> publicApi.getHotPosts(currentSubreddit, token = "")
        SortMode.NEW -> publicApi.getNewPosts(currentSubreddit, token = "")
        SortMode.TOP -> publicApi.getTopPosts(currentSubreddit, token = "")
    }
}
