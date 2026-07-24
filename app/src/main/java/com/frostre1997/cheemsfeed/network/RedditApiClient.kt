package com.frostre1997.cheemsfeed.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RedditApiClient {

    private const val REDDIT_WWW_BASE = "https://www.reddit.com/"
    private const val REDDIT_OAUTH_BASE = "https://oauth.reddit.com/"

    // Cookie jar – persistent storage
    private val cookieJar = RedditCookieJar()

    // User-Agent: Mimics a browser
    private val userAgentInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "android:com.frostre1997.cheemsfeed:v1.0 (by /u/frostre1997)")
            .header("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    // Warm-up interceptor: retry after 403
    private val warmUpInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        if (response.code == 403) {
            response.close()
            // Warm-up request to get fresh cookies
            val warmUpRequest = chain.request().newBuilder()
                .url("https://www.reddit.com/")
                .build()
            val warmUpResponse = chain.proceed(warmUpRequest)
            warmUpResponse.close()
            // Retry original
            return@Interceptor chain.proceed(chain.request())
        }
        response
    }

    // Logging – always log for debugging (no BuildConfig dependency)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(warmUpInterceptor)
            .addInterceptor(loggingInterceptor)
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // PUBLIC SERVICE – no auth, reads from www.reddit.com
    val publicService: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl(REDDIT_WWW_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)
    }

    // OAUTH SERVICE – for future use (needs Client ID/Secret)
    val oauthService: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl(REDDIT_OAUTH_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)
    }

    // Clear all stored cookies (logout)
    fun clearCookies() {
        cookieJar.clearCookies()
    }
}
