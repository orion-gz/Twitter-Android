package orion.app.twitter.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val userId: String,
    val username: String,
    @SerializedName("display_name")
    val displayName: String,
    val email: String,
    @SerializedName("hashed_password")
    val hashedPassword: String,
    val bio: String?,
    @SerializedName("profile_image_url")
    val profileImageUrl: String?,
    @SerializedName("profile_banner_url")
    val profileBannerUrl: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("following_count")
    val followingCount: Int,
    @SerializedName("follower_count")
    val followerCount: Int,
    @SerializedName("tweet_count")
    val tweetCount: Int,
    @SerializedName("is_following")
    var isFollowing: Boolean = false
)