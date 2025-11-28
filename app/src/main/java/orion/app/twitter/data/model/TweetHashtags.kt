package orion.app.twitter.data.model

import com.google.gson.annotations.SerializedName

data class TweetHashtags(
    @SerializedName("tweet_id")
    val tweetId: Long,
    @SerializedName("hashtag_id")
    val hashtagId: Long
)
