package orion.app.twitter.data.remote

import orion.app.twitter.data.model.Tweet
import orion.app.twitter.data.model.User
import orion.app.twitter.data.model.request.LoginRequest
import orion.app.twitter.data.model.request.SignUpRequest
import orion.app.twitter.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TwitterApiService {

    @POST("signup")
    suspend fun signup(@Body request: SignUpRequest): Response<User>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<List<User>>

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<User>

    @GET("tweets")
    suspend fun getTimeline(): Response<List<Tweet>>

    @GET("users/{userId}/tweets")
    suspend fun getTweetsForUser(@Path("userId") userId: String): Response<List<Tweet>>

    @POST("users/{userId}/follow")
    suspend fun followUser(@Path("userId") userId: String): Response<Unit>

    @POST("users/{userId}/unfollow")
    suspend fun unfollowUser(@Path("userId") userId: String): Response<Unit>

    @GET("users/{userId}/replies")
    suspend fun getRepliesForUser(@Path("userId") userId: String): Response<List<Tweet>>

    @GET("users/{userId}/likes")
    suspend fun getLikesForUser(@Path("userId") userId: String): Response<List<Tweet>>

    @GET("tweets/{tweetId}")
    suspend fun getTweet(@Path("tweetId") tweetId: String): Response<Tweet>

    @POST("tweets")
    suspend fun createTweet(@Body tweet: Tweet): Response<Tweet>

    @DELETE("tweets/{tweetId}")
    suspend fun deleteTweet(@Path("tweetId") tweetId: String): Response<Unit>

    @POST("tweets/{tweetId}/like")
    suspend fun likeTweet(@Path("tweetId") tweetId: String): Response<Unit>

    @POST("tweets/{tweetId}/unlike")
    suspend fun unlikeTweet(@Path("tweetId") tweetId: String): Response<Unit>

    @POST("tweets/{tweetId}/retweet")
    suspend fun retweetTweet(@Path("tweetId") tweetId: String): Response<Unit>

    @POST("tweets/{tweetId}/unretweet")
    suspend fun unretweetTweet(@Path("tweetId") tweetId: String): Response<Unit>

    @GET("tweets/{tweetId}/replies")
    suspend fun getReplies(@Path("tweetId") tweetId: String): Response<List<Tweet>>

    @POST("tweets/{tweetId}/reply")
    suspend fun postReply(@Path("tweetId") tweetId: String, @Body tweet: Tweet): Response<Tweet>
}
