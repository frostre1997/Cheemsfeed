package com.frostre1997.cheemsfeed

data class RedditResponse(
    val data: RedditData
)

data class RedditData(
    val children: List<RedditChild>
)

data class RedditChild(
    val data: PostData
)

data class PostData(
    val title: String,
    val author: String?,
    val url: String?,
    val thumbnail: String?,
    val created_utc: Long?,
    val num_comments: Int?,
    val score: Int?
    // add any other fields you need
)
