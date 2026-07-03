package com.frostre1997.cheemsfeed

data class RedditResponse(val data: RedditData)
data class RedditData(val children: List<RedditChildren>)
data class RedditChildren(val data: RedditPost)
