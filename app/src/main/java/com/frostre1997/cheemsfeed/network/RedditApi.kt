package com.frostre1997.cheemsfeed.network

import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Path

interface RedditApi {

    // ============ PUBLIC JSON ENDPOINTS (No Auth Required) ============
    // These work like a browser — no Client ID needed

    @GET("r/{subreddit}/hot.json")
    suspend fun getHotPosts(
        @Path("subreddit") subreddit: String = "all",
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null
    ): Response<PublicFeedResponse>

    @GET("r/{subreddit}/new.json")
    suspend fun getNewPosts(
        @Path("subreddit") subreddit: String = "all",
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null
    ): Response<PublicFeedResponse>

    @GET("r/{subreddit}/top.json")
    suspend fun getTopPosts(
        @Path("subreddit") subreddit: String = "all",
        @Query("limit") limit: Int = 25,
        @Query("t") time: String = "day", // hour, day, week, month, year, all
        @Query("after") after: String? = null
    ): Response<PublicFeedResponse>

    @GET("r/{subreddit}/rising.json")
    suspend fun getRisingPosts(
        @Path("subreddit") subreddit: String = "all",
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null
    ): Response<PublicFeedResponse>

    @GET("r/{subreddit}/comments/{postId}/{postTitle}.json")
    suspend fun getComments(
        @Path("subreddit") subreddit: String,
        @Path("postId") postId: String,
        @Path("postTitle") postTitle: String
    ): Response<List<PublicCommentResponse>>

    @GET("user/{username}.json")
    suspend fun getUserPosts(
        @Path("username") username: String,
        @Query("limit") limit: Int = 25
    ): Response<PublicFeedResponse>

    // ============ LEGACY / OAuth ENDPOINTS (Keep for later) ============
    // These require a Client ID and Secret — you can add them later if Reddit approves

    @GET("api/v1/me")
    suspend fun getMe(
        @Header("Authorization") auth: String
    ): Response<RedditUser>

    @POST("api/vote")
    suspend fun vote(
        @Header("Authorization") auth: String,
        @Field("id") id: String,
        @Field("dir") dir: Int
    ): Response<Unit>

    @POST("api/comment")
    suspend fun comment(
        @Header("Authorization") auth: String,
        @Field("thing_id") thingId: String,
        @Field("text") text: String
    ): Response<Unit>

    @POST("api/submit")
    suspend fun submit(
        @Header("Authorization") auth: String,
        @Field("sr") subreddit: String,
        @Field("title") title: String,
        @Field("text") text: String? = null,
        @Field("url") url: String? = null,
        @Field("kind") kind: String = "self"
    ): Response<Unit>
}
