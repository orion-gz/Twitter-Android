package orion.app.twitter.data.repository

import orion.app.twitter.data.model.Tweet
import orion.app.twitter.data.model.User
import orion.app.twitter.data.model.request.LoginRequest
import orion.app.twitter.data.model.request.SignUpRequest
import orion.app.twitter.data.model.response.LoginResponse
import orion.app.twitter.data.remote.TwitterApiService

class TwitterRepository(private val twitterApiService: TwitterApiService) {

    suspend fun login(request: LoginRequest): LoginResponse? {
        val response = twitterApiService.login(request)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun signup(request: SignUpRequest): User? {
        val response = twitterApiService.signup(request)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun searchUsers(query: String): List<User>? {
        val response = twitterApiService.searchUsers(query)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getUser(userId: String): User? {
        val response = twitterApiService.getUser(userId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getTweet(tweetId: String): Tweet? {
        val response = twitterApiService.getTweet(tweetId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getRepliesForUser(userId: String): List<Tweet>? {
        val response = twitterApiService.getRepliesForUser(userId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getLikesForUser(userId: String): List<Tweet>? {
        val response = twitterApiService.getLikesForUser(userId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getTimeline(): List<Tweet>? {
        val response = twitterApiService.getTimeline()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getTweetsForUser(userId: String): List<Tweet>? {
        val response = twitterApiService.getTweetsForUser(userId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun followUser(userId: String): Boolean {
        return twitterApiService.followUser(userId).isSuccessful
    }

    suspend fun unfollowUser(userId: String): Boolean {
        return twitterApiService.unfollowUser(userId).isSuccessful
    }

    suspend fun likeTweet(tweetId: String): Boolean {
        return twitterApiService.likeTweet(tweetId).isSuccessful
    }

    suspend fun unlikeTweet(tweetId: String): Boolean {
        return twitterApiService.unlikeTweet(tweetId).isSuccessful
    }

    suspend fun retweetTweet(tweetId: String): Boolean {
        return twitterApiService.retweetTweet(tweetId).isSuccessful
    }

    suspend fun unretweetTweet(tweetId: String): Boolean {
        return twitterApiService.unretweetTweet(tweetId).isSuccessful
    }

    suspend fun createTweet(tweet: Tweet): Tweet? {
        val response = twitterApiService.createTweet(tweet)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun deleteTweet(tweetId: String): Boolean {
        return twitterApiService.deleteTweet(tweetId).isSuccessful
    }

    suspend fun getReplies(tweetId: String): List<Tweet>? {
        val response = twitterApiService.getReplies(tweetId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun postReply(tweetId: String, tweet: Tweet): Tweet? {
        val response = twitterApiService.postReply(tweetId, tweet)
        return if (response.isSuccessful) response.body() else null
    }
}
