package orion.app.twitter.data.model

import com.google.gson.annotations.SerializedName

data class Hashtags(
    @SerializedName("hashtag_id")
    val hashtagId: Long,
    @SerializedName("tag_text")
    val tagText: String
)
