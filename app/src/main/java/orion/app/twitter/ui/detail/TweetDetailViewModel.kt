package orion.app.twitter.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import orion.app.twitter.data.model.Tweet
import orion.app.twitter.data.model.TweetWithUser
import orion.app.twitter.data.repository.TwitterRepository
import java.util.UUID

class TweetDetailViewModel(private val repository: TwitterRepository) : ViewModel() {

    private val _tweet = MutableLiveData<TweetWithUser>()
    val tweet: LiveData<TweetWithUser> = _tweet

    private val _replies = MutableLiveData<List<TweetWithUser>>()
    val replies: LiveData<List<TweetWithUser>> = _replies

    private var currentTweetId: String? = null

    fun fetchTweetDetail(tweetId: String) {
        currentTweetId = tweetId
        viewModelScope.launch {
            repository.getTweet(tweetId)?.let { tweet ->
                repository.getUser(tweet.userId)?.let { user ->
                    _tweet.postValue(TweetWithUser(tweet, user))
                }
            }

            val repliesList = repository.getReplies(tweetId)
            if (repliesList != null) {
                val repliesWithUsers = repliesList.mapNotNull { reply ->
                    repository.getUser(reply.userId)?.let { user ->
                        TweetWithUser(reply, user)
                    }
                }
                _replies.postValue(repliesWithUsers)
            }
        }
    }

    fun postReply(content: String) {
        val parentId = currentTweetId ?: return

        viewModelScope.launch {
            val userId = "my_temp_id"
            val replyTweet = Tweet(
                tweetId = UUID.randomUUID().toString(),
                userId = userId,
                content = content,
                createdAt = "",
                parentTweetId = parentId,
                quotedTweetId = null,
                replyCount = 0,
                likeCount = 0,
                retweetCount = 0
            )

            repository.postReply(parentId, replyTweet)?.let {
                fetchTweetDetail(parentId)
            }
        }
    }

    fun deleteTweet(tweetId: String) {
        viewModelScope.launch {
            repository.deleteTweet(tweetId)
        }
    }

    fun toggleLikeStatus(targetTweetId: String) {
        val mainTweet = _tweet.value?.tweet
        if (mainTweet?.tweetId == targetTweetId) {
            toggleLike(mainTweet) { updatedTweet ->
                _tweet.postValue(_tweet.value!!.copy(tweet = updatedTweet))
            }
            return
        }

        val targetReply = _replies.value?.find { it.tweet.tweetId == targetTweetId }
        if (targetReply != null) {
            toggleLike(targetReply.tweet) { updatedTweet ->
                val updatedList = _replies.value?.map {
                    if (it.tweet.tweetId == targetTweetId) it.copy(tweet = updatedTweet) else it
                }
                _replies.postValue(updatedList!!)
            }
        }
    }

    fun toggleRetweetStatus(targetTweetId: String) {
        val mainTweet = _tweet.value?.tweet
        if (mainTweet?.tweetId == targetTweetId) {
            toggleRetweet(mainTweet) { updatedTweet ->
                _tweet.postValue(_tweet.value!!.copy(tweet = updatedTweet))
            }
            return
        }

        val targetReply = _replies.value?.find { it.tweet.tweetId == targetTweetId }
        if (targetReply != null) {
            toggleRetweet(targetReply.tweet) { updatedTweet ->
                val updatedList = _replies.value?.map {
                    if (it.tweet.tweetId == targetTweetId) it.copy(tweet = updatedTweet) else it
                }
                _replies.postValue(updatedList!!)
            }
        }
    }

    private fun toggleLike(tweet: Tweet, onComplete: (Tweet) -> Unit) {
        viewModelScope.launch {
            val success = if (tweet.isLiked) repository.unlikeTweet(tweet.tweetId) else repository.likeTweet(tweet.tweetId)
            if (success) {
                val updatedTweet = tweet.copy(
                    isLiked = !tweet.isLiked,
                    likeCount = if (tweet.isLiked) tweet.likeCount - 1 else tweet.likeCount + 1
                )
                onComplete(updatedTweet)
            }
        }
    }

    private fun toggleRetweet(tweet: Tweet, onComplete: (Tweet) -> Unit) {
        viewModelScope.launch {
            val success = if (tweet.isRetweeted) repository.unretweetTweet(tweet.tweetId) else repository.retweetTweet(tweet.tweetId)
            if (success) {
                val updatedTweet = tweet.copy(
                    isRetweeted = !tweet.isRetweeted,
                    retweetCount = if (tweet.isRetweeted) tweet.retweetCount - 1 else tweet.retweetCount + 1
                )
                onComplete(updatedTweet)
            }
        }
    }
}