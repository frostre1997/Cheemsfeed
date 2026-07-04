package com.frostre1997.cheemsfeed.auth

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.frostre1997.cheemsfeed.network.RedditApi
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class RedditAuthManager private constructor(
    private val context: Context,
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

        suspend fun create(context: Context, wwwApi: RedditApi): RedditAuthManager =
            withContext(Dispatchers.IO) {
                RedditAuthManager(context.applicationContext, wwwApi).also { it.initPrefs() }
            }
    }

    @Volatile
    private var prefs: android.content.SharedPreferences? = null

    private fun initPrefs() {
        if (prefs != null) return
        synchronized(this) {
            if (prefs != null) return
            prefs = try {
                val key = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                EncryptedSharedPreferences.create(
                    context, PREFS_NAME, key,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            }
        }
    }

    fun getAuthorizationUrl(): String {
        val state = UUID.randomUUID().toString()
        prefs?.edit()?.putString("oauth_state", state)?.apply()
        return "$AUTH_BASE?" +
                "client_id=$REDDIT_CLIENT_ID&" +
                "response_type=code&" +
                "state=$state&" +
                "redirect_uri=$REDIRECT_URI&" +
                "duration=permanent&" +
                "scope=identity,read,mysubreddits"
    }

    fun getSavedState(): String? = prefs?.getString("oauth_state", null)

    fun clearState() {
        prefs?.edit()?.remove("oauth_state")?.apply()
    }

    suspend fun exchangeCodeForToken(code: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val credentials = "$REDDIT_CLIENT_ID:$REDDIT_CLIENT_SECRET"
            val authHeader = "Basic " + Base64.encodeToString(
                credentials.toByteArray(), Base64.NO_WRAP
            )
            val body = JsonObject().apply {
                addProperty("grant_type", "authorization_code")
                addProperty("code", code)
                addProperty("redirect_uri", REDIRECT_URI)
            }
            val response = wwwApi.getAccessToken(authHeader, body)
            storeTokens(response)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun refreshAccessToken(): Boolean = withContext(Dispatchers.IO) {
        val refreshToken = prefs?.getString(KEY_REFRESH_TOKEN, null) ?: return@withContext false
        try {
            val credentials = "$REDDIT_CLIENT_ID:$REDDIT_CLIENT_SECRET"
            val authHeader = "Basic " + Base64.encodeToString(
                credentials.toByteArray(), Base64.NO_WRAP
            )
            val body = JsonObject().apply {
                addProperty("grant_type", "refresh_token")
                addProperty("refresh_token", refreshToken)
            }
            val response = wwwApi.getAccessToken(authHeader, body)
            storeTokens(response)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getValidAccessToken(): String? = withContext(Dispatchers.IO) {
        val expiry = prefs?.getLong(KEY_TOKEN_EXPIRY, 0) ?: 0L
        if (System.currentTimeMillis() + 60000 > expiry) {
            val success = refreshAccessToken()
            if (!success && getAccessToken() == null) return@withContext null
        }
        getAccessToken()
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun getUserName(): String? = prefs?.getString(KEY_USERNAME, null)

    fun logout() {
        prefs?.edit()?.clear()?.apply()
    }

    private fun storeTokens(response: com.frostre1997.cheemsfeed.model.TokenResponse) {
        val expiryTime = System.currentTimeMillis() + (response.expires_in * 1000)
        prefs?.edit()?.apply {
            putString(KEY_ACCESS_TOKEN, response.access_token)
            putLong(KEY_TOKEN_EXPIRY, expiryTime)
            response.refresh_token?.let { putString(KEY_REFRESH_TOKEN, it) }
            apply()
        }
    }

    private fun getAccessToken(): String? = prefs?.getString(KEY_ACCESS_TOKEN, null)
}
