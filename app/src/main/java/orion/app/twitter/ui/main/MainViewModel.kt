package orion.app.twitter.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import orion.app.twitter.data.model.TweetWithUser
import orion.app.twitter.data.model.User
import orion.app.twitter.data.repository.TwitterRepository

class MainViewModel(private val repository: TwitterRepository) : ViewModel() {
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _tweets = MutableLiveData<List<TweetWithUser>>()
    val tweets: LiveData<List<TweetWithUser>> = _tweets

    fun fetchCurrentUser(userId: String) {
        viewModelScope.launch {
            val user = repository.getUser(userId)
            if (user != null) {
                _currentUser.postValue(user)
            }
        }
    }

    fun fetchTweetsAndUsers() {
        viewModelScope.launch {
            val tweets = repository.getTimeline()
            if (tweets != null) {
                val tweetsWithUsers = tweets.mapNotNull { tweet ->
                    repository.getUser(tweet.userId)?.let { user ->
                        TweetWithUser(tweet, user)
                    }
                }
                _tweets.postValue(tweetsWithUsers)
            }
        }
    }

    fun deleteTweet(tweetId: String) {
        viewModelScope.launch {
            val success = repository.deleteTweet(tweetId)
            if (success) {
                val updatedTweets = _tweets.value?.filter { it.tweet.tweetId != tweetId }
                updatedTweets?.let { _tweets.postValue(it) }
            }
        }
    }

    fun toggleLikeStatus(tweetId: String) {
        viewModelScope.launch {
            val currentTweets = _tweets.value ?: return@launch
            val tweetToUpdate = currentTweets.find { it.tweet.tweetId == tweetId } ?: return@launch

            val isCurrentlyLiked = tweetToUpdate.tweet.isLiked
            val success = if (isCurrentlyLiked) {
                repository.unlikeTweet(tweetId)
            } else {
                repository.likeTweet(tweetId)
            }

            if (success) {
                val updatedTweet = tweetToUpdate.tweet.copy(
                    isLiked = !isCurrentlyLiked,
                    likeCount = if (isCurrentlyLiked) tweetToUpdate.tweet.likeCount - 1 else tweetToUpdate.tweet.likeCount + 1
                )
                val updatedTweets = currentTweets.map {
                    if (it.tweet.tweetId == tweetId) {
                        it.copy(tweet = updatedTweet)
                    } else {
                        it
                    }
                }
                _tweets.postValue(updatedTweets)
            }
        }
    }

    fun toggleRetweetStatus(tweetId: String) {
        viewModelScope.launch {
            val currentTweets = _tweets.value ?: return@launch
            val tweetToUpdate = currentTweets.find { it.tweet.tweetId == tweetId } ?: return@launch

            val isCurrentlyRetweeted = tweetToUpdate.tweet.isRetweeted
            val success = if (isCurrentlyRetweeted) {
                repository.unretweetTweet(tweetId)
            } else {
                repository.retweetTweet(tweetId)
            }

            if (success) {
                val updatedTweet = tweetToUpdate.tweet.copy(
                    isRetweeted = !isCurrentlyRetweeted,
                    retweetCount = if (isCurrentlyRetweeted) tweetToUpdate.tweet.retweetCount - 1 else tweetToUpdate.tweet.retweetCount + 1
                )
                val updatedTweets = currentTweets.map {
                    if (it.tweet.tweetId == tweetId) {
                        it.copy(tweet = updatedTweet)
                    } else {
                        it
                    }
                }
                _tweets.postValue(updatedTweets)
            }
        }
    }
}
