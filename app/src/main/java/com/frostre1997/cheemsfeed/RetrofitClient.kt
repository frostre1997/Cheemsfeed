package com.frostre1997.cheemsfeed

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RedditApi {
    @GET("r/cheemsburbger/hot.json")
    fun getHotPosts(): Call<RedditResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://www.reddit.com/"

    val instance: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)
    }
}
