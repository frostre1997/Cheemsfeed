package com.frostre1997.cheemsfeed.network

import com.google.gson.annotations.SerializedName

// ===== FEED RESPONSE =====
data class PublicFeedResponse(
    val kind: String,
    val data: PublicFeedData
)

data class PublicFeedData(
    val children: List<PublicPostChild>,
    val after: String?,
    val before: String?,
    val dist: Int
)

data class PublicPostChild(
    val kind: String,
    val data: PublicPost
)

// ===== POST DATA =====
data class PublicPost(
    val id: String,
    val name: String,                    // t3_xxx format for voting
    val title: String,
    val url: String?,
    val permalink: String?,
    val score: Int,
    @SerializedName("num_comments")
    val numComments: Int,
    val subreddit: String,
    @SerializedName("subreddit_name_prefixed")
    val subredditPrefixed: String,
    @SerializedName("created_utc")
    val createdUtc: Double,
    val thumbnail: String?,
    val selftext: String?,
    val author: String?,
    @SerializedName("is_self")
    val isSelf: Boolean,
    @SerializedName("is_video")
    val isVideo: Boolean,
    @SerializedName("post_hint")
    val postHint: String?,
    @SerializedName("preview")
    val preview: Preview?,
    @SerializedName("media")
    val media: Media?,
    @SerializedName("media_metadata")
    val mediaMetadata: Map<String, MediaMetadata>?,
    @SerializedName("secure_media")
    val secureMedia: Media?,
    @SerializedName("upvote_ratio")
    val upvoteRatio: Double,
    val downs: Int,
    val ups: Int,
    @SerializedName("total_awards_received")
    val totalAwards: Int,
    @SerializedName("awarders")
    val awarders: List<String>?,
    @SerializedName("stickied")
    val isStickied: Boolean,
    @SerializedName("locked")
    val isLocked: Boolean,
    @SerializedName("over_18")
    val isNSFW: Boolean,
    @SerializedName("spoiler")
    val isSpoiler: Boolean,
    @SerializedName("pinned")
    val isPinned: Boolean
)

// ===== PREVIEW IMAGES =====
data class Preview(
    val images: List<Image>,
    val enabled: Boolean
)

data class Image(
    val source: ImageSource,
    val resolutions: List<ImageSource>,
    val id: String
)

data class ImageSource(
    val url: String,
    val width: Int,
    val height: Int
)

data class Media(
    @SerializedName("reddit_video")
    val redditVideo: RedditVideo?,
    val type: String?,
    val oembed: OEmbed?
)

data class RedditVideo(
    @SerializedName("fallback_url")
    val fallbackUrl: String,
    val height: Int,
    val width: Int,
    @SerializedName("scrubber_media_url")
    val scrubberMediaUrl: String,
    @SerializedName("dash_url")
    val dashUrl: String,
    val duration: Int,
    @SerializedName("hls_url")
    val hlsUrl: String,
    @SerializedName("is_gif")
    val isGif: Boolean
)

data class OEmbed(
    val title: String?,
    val author_name: String?,
    val author_url: String?,
    val provider_name: String?,
    val thumbnail_url: String?,
    val type: String?,
    val html: String?,
    val width: Int?,
    val height: Int?
)

data class MediaMetadata(
    val e: String, // "Image", "Video", "AnimatedImage", etc.
    val id: String,
    val m: String, // MIME type
    val s: ImageSource?,
    val status: String?
)

// ===== COMMENTS RESPONSE =====
data class PublicCommentResponse(
    val kind: String,
    val data: CommentListingData
)

data class CommentListingData(
    val children: List<CommentChild>,
    val after: String?,
    val before: String?
)

data class CommentChild(
    val kind: String,
    val data: PublicComment
)

data class PublicComment(
    val id: String,
    val name: String, // t1_xxx format for voting
    val body: String,
    val author: String?,
    val score: Int,
    val downs: Int,
    val ups: Int,
    @SerializedName("created_utc")
    val createdUtc: Double,
    @SerializedName("replies")
    val replies: Replies?,
    @SerializedName("parent_id")
    val parentId: String?,
    @SerializedName("link_id")
    val linkId: String,
    @SerializedName("is_submitter")
    val isSubmitter: Boolean,
    @SerializedName("is_stickied")
    val isStickied: Boolean,
    @SerializedName("controversiality")
    val controversiality: Int,
    @SerializedName("body_html")
    val bodyHtml: String?,
    @SerializedName("score_hidden")
    val scoreHidden: Boolean
)

data class Replies(
    val kind: String,
    val data: CommentListingData
)

// ===== USER DATA (for OAuth later) =====
data class RedditUser(
    val id: String,
    val name: String,
    val icon_img: String?,
    val created_utc: Double,
    val link_karma: Int,
    val comment_karma: Int,
    val is_gold: Boolean,
    val is_mod: Boolean
)
