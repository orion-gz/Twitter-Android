package orion.app.twitter.data.model

import com.google.gson.annotations.SerializedName

data class Retweets(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("tweet_id")
    val tweetId: Long,
    @SerializedName("created_at")
    val createdAt: String
)
