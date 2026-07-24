package com.frostre1997.cheemsfeed.auth

import android.content.Context
import android.content.SharedPreferences
import com.frostre1997.cheemsfeed.network.RedditApi
import com.frostre1997.cheemsfeed.network.RedditApiClient

class RedditAuthManager(
    private val context: Context,
    private val apiService: RedditApi
) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("reddit_auth", Context.MODE_PRIVATE)

    // Save OAuth tokens (for later when you get approval)
    fun saveTokens(accessToken: String, refreshToken: String?) {
        prefs.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    // Get stored access token (returns null if not logged in)
    fun getAccessToken(): String? = prefs.getString("access_token", null)

    // Check if user is logged in (via OAuth)
    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    // Clear all auth data (logout)
    fun logout() {
        prefs.edit().clear().apply()
        // Also clear cookies from the cookie jar
        RedditApiClient.clearCookies() // we'll add this method
    }

    // Refresh login state (called on resume) – just check prefs
    fun refreshLoginState() {
        // Nothing needed for public API; but if you had OAuth, you'd refresh token here
    }
}
