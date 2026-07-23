package com.frostre1997.cheemsfeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.frostre1997.cheemsfeed.auth.RedditAuthManager
import com.frostre1997.cheemsfeed.network.RedditApi

class FeedViewModelFactory(
    private val publicService: RedditApi,
    private val authManager: RedditAuthManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(publicService, authManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
