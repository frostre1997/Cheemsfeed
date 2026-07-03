package com.frostre1997.cheemsfeed

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApi {
    @GET("r/{subreddit}/hot.json")
    fun getHotPosts(
        @Path("subreddit") subreddit: String = "Doge",
        @Query("limit") limit: Int = 25
    ): Call<RedditResponse>
}
