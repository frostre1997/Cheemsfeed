package com.frostre1997.cheemsfeed.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RedditApiClient {

    private const val REDDIT_OAUTH_BASE = "https://oauth.reddit.com/"
    private const val REDDIT_WWW_BASE = "https://www.reddit.com/"

    private val userAgentInterceptor = { chain: okhttp3.Interceptor.Chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "android:com.frostre1997.cheemsfeed:v1.0 (by /u/frostre1997)")
            .build()
        chain.proceed(request)
    }

    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val oauthService: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl(REDDIT_OAUTH_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)
    }

    val wwwService: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl(REDDIT_WWW_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)
    }

    val publicService: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl(REDDIT_WWW_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)
    }
}
