package com.frostre1997.cheemsfeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frostre1997.cheemsfeed.network.PublicPost
import com.frostre1997.cheemsfeed.network.RedditApiClient
import com.frostre1997.cheemsfeed.network.PublicFeedResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FeedUiState(
    val isLoading: Boolean = false,
    val posts: List<PublicPost> = emptyList(),
    val error: String? = null,
    val after: String? = null,
    val hasMore: Boolean = true
)

enum class SortMode {
    HOT, NEW, TOP, RISING
}

class FeedViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var currentSubreddit = "all"
    private var currentSort = SortMode.HOT

    fun loadFeed(
        subreddit: String = currentSubreddit,
        sort: SortMode = currentSort,
        loadMore: Boolean = false
    ) {
        currentSubreddit = subreddit
        currentSort = sort

        if (!loadMore) {
            _uiState.value = FeedUiState(isLoading = true)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val after = if (loadMore) _uiState.value.after else null
                val response = when (sort) {
                    SortMode.HOT -> RedditApiClient.publicService.getHotPosts(subreddit, 25, after)
                    SortMode.NEW -> RedditApiClient.publicService.getNewPosts(subreddit, 25, after)
                    SortMode.TOP -> RedditApiClient.publicService.getTopPosts(subreddit, 25, "day", after)
                    SortMode.RISING -> RedditApiClient.publicService.getRisingPosts(subreddit, 25, after)
                }

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val posts = data.data.children.map { it.data }
                        val newPosts = if (loadMore) {
                            _uiState.value.posts + posts
                        } else {
                            posts
                        }
                        _uiState.value = FeedUiState(
                            isLoading = false,
                            posts = newPosts,
                            error = null,
                            after = data.data.after,
                            hasMore = data.data.after != null
                        )
                    } else {
                        _uiState.value = FeedUiState(
                            isLoading = false,
                            posts = if (loadMore) _uiState.value.posts else emptyList(),
                            error = "No data received"
                        )
                    }
                } else {
                    _uiState.value = FeedUiState(
                        isLoading = false,
                        posts = if (loadMore) _uiState.value.posts else emptyList(),
                        error = "Error: ${response.code()} - ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FeedUiState(
                    isLoading = false,
                    posts = if (loadMore) _uiState.value.posts else emptyList(),
                    error = "Network error: ${e.message}"
                )
            }
        }
    }

    fun setSortMode(sort: SortMode) {
        currentSort = sort
        loadFeed(currentSubreddit, sort)
    }

    fun loadMore() {
        if (_uiState.value.hasMore && !_uiState.value.isLoading) {
            loadFeed(currentSubreddit, currentSort, loadMore = true)
        }
    }

    fun refresh() {
        loadFeed(currentSubreddit, currentSort)
    }
}
