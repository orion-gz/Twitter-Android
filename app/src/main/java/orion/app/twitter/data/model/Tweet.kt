package orion.app.twitter.data.model

import com.google.gson.annotations.SerializedName

data class Tweet(
    @SerializedName("tweet_id")
    val tweetId: String,
    @SerializedName("user_id")
    val userId: String,
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("parent_tweet_id")
    val parentTweetId: String?,
    @SerializedName("quoted_tweet_id")
    val quotedTweetId: String?,
    @SerializedName("reply_count")
    val replyCount: Int,
    @SerializedName("like_count")
    var likeCount: Int,
    @SerializedName("retweet_count")
    var retweetCount: Int,
    @SerializedName("is_liked")
    var isLiked: Boolean = false,
    @SerializedName("is_retweeted")
    var isRetweeted: Boolean = false
)