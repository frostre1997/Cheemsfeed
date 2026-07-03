package com.frostre1997.cheemsfeed.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_posts")
data class SavedPost(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val subreddit: String,
    val score: Long,
    val commentCount: Int,
    val createdAt: Long,
    val postUrl: String,
    val thumbnailUrl: String,
    val isSelfPost: Boolean,
    val permalink: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val username: String,
    val userId: String,
    val linkKarma: Long,
    val commentKarma: Long,
    val isGold: Boolean,
    val profileImageUrl: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
