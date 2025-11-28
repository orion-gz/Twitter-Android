package orion.app.twitter.data.model

import com.google.gson.annotations.SerializedName

data class Media(
    @SerializedName("media_id")
    val mediaId: Long,
    @SerializedName("tweet_id")
    val tweetId: Long,
    @SerializedName("media_url")
    val mediaUrl: String,
    @SerializedName("media_type")
    val mediaType: String // Assuming ENUM as String
)
