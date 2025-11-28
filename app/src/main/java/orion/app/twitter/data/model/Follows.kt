package orion.app.twitter.data.model

import com.google.gson.annotations.SerializedName

data class Follows(
    @SerializedName("follower_id")
    val followerId: Long,
    @SerializedName("following_id")
    val followingId: Long,
    @SerializedName("created_at")
    val createdAt: String
)
