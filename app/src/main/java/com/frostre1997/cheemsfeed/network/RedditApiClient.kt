package com.frostre1997.cheemsfeed.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RedditApiClient {
    private val cookieJar = RedditCookieJar()
    // ... then in createClient():
    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)  // use the same instance

    private const val REDDIT_WWW_BASE = "https://www.reddit.com/"
    private const val REDDIT_OAUTH_BASE = "https://oauth.reddit.com/"

    // ===== INTERCEPTORS =====

    // 1. User-Agent: Mimics a browser (critical for public JSON access)
    private val userAgentInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "android:com.frostre1997.cheemsfeed:v1.0 (by /u/frostre1997)")
            .header("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    // 2. Cookie Manager: Stores and persists cookies (important for session stability)
    private val cookieInterceptor = Interceptor { chain ->
        val request = chain.request()
        // Cookies are managed automatically by OkHttp's CookieJar
        // We just let it handle it
        chain.proceed(request)
    }

    // 3. Warm-up Interceptor: If we get a 403, try to warm up the connection
    private val warmUpInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        if (response.code == 403) {
            // Close the failed response
            response.close()
            // Try a warm-up request to reddit.com to get fresh cookies
            val warmUpRequest = chain.request().newBuilder()
                .url("https://www.reddit.com/")
                .build()
            val warmUpResponse = chain.proceed(warmUpRequest)
            warmUpResponse.close()
            // Retry the original request
            return@Interceptor chain.proceed(chain.request())
        }
        response
    }

    // 4. Logging (for debugging)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    // ===== HTTP CLIENT =====

    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(cookieInterceptor)
            .addInterceptor(warmUpInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cookieJar(RedditCookieJar()) // Persistent cookie storage
            .build()
    }

    // ===== RETROFIT SERVICES =====

    /**
     * PUBLIC SERVICE: No auth required, works like a browser.
     * Use this for reading posts, comments, and user profiles.
     */
    val publicService: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl(REDDIT_WWW_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)
    }

    /**
     * OAUTH SERVICE: Requires a Client ID and Secret.
     * Use this for authenticated actions (voting, posting, commenting).
     * Keep this for later when you get Reddit approval.
     */
    val oauthService: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl(REDDIT_OAUTH_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)

    fun clearCookies() {
        cookieJar.clearCookies()
    }
}
