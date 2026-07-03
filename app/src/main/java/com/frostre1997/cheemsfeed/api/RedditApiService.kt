package com.frostre1997.cheemsfeed.api

import com.google.gson.JsonObject
import retrofit2.http.*

interface RedditApiService {
    
    // OAuth2 Token endpoint
    @POST("api/v1/access_token")
    suspend fun getAccessToken(
        @Header("Authorization") auth: String,
        @Body body: JsonObject
    ): TokenResponse
    
    // Get current user info
    @GET("api/v1/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): UserResponse
    
    // Get hot posts
    @GET("r/{subreddit}/hot.json")
    suspend fun getHotPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null,
        @Header("Authorization") token: String
    ): PostListResponse
    
    // Get new posts
    @GET("r/{subreddit}/new.json")
    suspend fun getNewPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null,
        @Header("Authorization") token: String
    ): PostListResponse
    
    // Get top posts
    @GET("r/{subreddit}/top.json")
    suspend fun getTopPosts(
        @Path("subreddit") subreddit: String,
        @Query("t") timeFilter: String = "day",
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null,
        @Header("Authorization") token: String
    ): PostListResponse
}

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val scope: String
)

data class UserResponse(
    val name: String,
    val id: String,
    val link_karma: Long,
    val comment_karma: Long,
    val is_gold: Boolean
)

data class PostListResponse(
    val data: PostData
)

data class PostData(
    val children: List<PostWrapper>,
    val after: String?,
    val before: String?
)

data class PostWrapper(
    val data: Post
)

data class Post(
    val id: String,
    val title: String,
    val selftext: String,
    val author: String,
    val subreddit: String,
    val score: Long,
    val num_comments: Int,
    val created_utc: Long,
    val url: String,
    val thumbnail: String,
    val is_self: Boolean,
    val permalink: String
)
