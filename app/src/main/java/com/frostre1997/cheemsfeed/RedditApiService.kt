package com.frostre1997.cheemsfeed

import retrofit2.Call
import retrofit2.http.GET

interface RedditApiService {
    @GET("r/hot.json")
    fun getHotPosts(): Call<RedditResponse>
}

