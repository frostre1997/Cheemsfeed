package com.frostre1997.cheemsfeed.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.frostre1997.cheemsfeed.network.RedditApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class RedditAuthManager(
    context: Context,
    private val wwwApi: RedditApi
) {
    companion object {
        private const val REDDIT_CLIENT_ID = "YOUR_REDDIT_CLIENT_ID"
        private const val REDDIT_CLIENT_SECRET = "YOUR_REDDIT_CLIENT_SECRET"
        private const val REDIRECT_URI = "cheemsfeed://auth"
        private const val AUTH_BASE = "https://www.reddit.com/api/v1/authorize"

        private const val PREFS_NAME = "reddit_auth"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_USERNAME = "username"
    }

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAuthorizationUrl(): String {
        val state = UUID.randomUUID().toString()
        prefs.edit().putString("oauth_state", state).apply()
        return "$AUTH_BASE?" +
                "client_id=$REDDIT_CLIENT_ID&" +
                "response_type=code&" +
                "state=$state&" +
                "redirect_uri=$REDIRECT_URI&" +
                "duration=permanent&" +
                "scope=identity,read,mysubreddits"
    }

    fun getSavedState(): String? = prefs.getString("oauth_state", null)

    fun clearState() {
        prefs.edit().remove("oauth_state").apply()
    }

    suspend fun exchangeCodeForToken(code: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val credentials = "$REDDIT_CLIENT_ID:$REDDIT_CLIENT_SECRET"
            val authHeader = "Basic " + Base64.encodeToString(
                credentials.toByteArray(), Base64.NO_WRAP
            )
            val response = wwwApi.getAccessToken(
                authHeader,
                grantType = "authorization_code",
                code = code,
                redirectUri = REDIRECT_URI
            )
            storeTokens(response)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun refreshAccessToken(): Boolean = withContext(Dispatchers.IO) {
        val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null) ?: return@withContext false
        try {
            val credentials = "$REDDIT_CLIENT_ID:$REDDIT_CLIENT_SECRET"
            val authHeader = "Basic " + Base64.encodeToString(
                credentials.toByteArray(), Base64.NO_WRAP
            )
            val response = wwwApi.getAccessToken(
                authHeader,
                grantType = "refresh_token",
                refreshToken = refreshToken
            )
            storeTokens(response)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getValidAccessToken(): String? = withContext(Dispatchers.IO) {
        val expiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        if (System.currentTimeMillis() + 60000 > expiry) {
            val success = refreshAccessToken()
            if (!success && getAccessToken() == null) return@withContext null
        }
        getAccessToken()
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun logout() {
        prefs.edit().clear().apply()
    }

    private fun storeTokens(response: com.frostre1997.cheemsfeed.model.TokenResponse) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, response.access_token)
            putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + response.expires_in * 1000)
            response.refresh_token?.let { putString(KEY_REFRESH_TOKEN, it) }
            apply()
        }
    }

    private fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
}
