package com.frostre1997.cheemsfeed

import retrofit2.http.GET
import retrofit2.http.Query

interface RedditApi {
    @GET("r/{subreddit}/hot.json")
    suspend fun getHotPosts(
        @Query("limit") limit: Int = 25
    ): RedditResponse
    
}

data class RedditResponse(
    val data: RedditData
)

data class RedditData(
    val children: List<RedditPost>
)

data class RedditPost(
    val data: PostData
)

data class PostData(
    val title: String,
    val selftext: String,
    val url: String,
    val author: String,
    val created_utc: Long,
    val ups: Int
)
