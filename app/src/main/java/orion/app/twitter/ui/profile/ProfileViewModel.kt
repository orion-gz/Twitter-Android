package orion.app.twitter.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import orion.app.twitter.data.model.Tweet
import orion.app.twitter.data.model.User
import orion.app.twitter.data.model.TweetWithUser
import orion.app.twitter.data.repository.TwitterRepository

class ProfileViewModel(private val repository: TwitterRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _tweets = MutableLiveData<List<TweetWithUser>>()
    val tweets: LiveData<List<TweetWithUser>> = _tweets

    private var currentUserId: String? = null

    fun fetchProfileData(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            repository.getUser(userId)?.let { user ->
                _user.value = user
                loadTabContent(0, user)
            }
        }
    }

    fun loadTabContent(tabIndex: Int, userParam: User? = null) {
        val userId = currentUserId ?: return
        val currentUserInfo = userParam ?: _user.value ?: return

        viewModelScope.launch {
            val tweetList = when (tabIndex) {
                0 -> repository.getTweetsForUser(userId)
                1 -> repository.getRepliesForUser(userId)
                2 -> repository.getLikesForUser(userId)
                else -> emptyList()
            }

            tweetList?.let { list ->
                val tweetsWithUsers = if (tabIndex == 2) {
                    list.mapNotNull { tweet ->
                        val author = repository.getUser(tweet.userId)
                        if (author != null) TweetWithUser(tweet, author) else null
                    }
                } else {
                    list.map { tweet ->
                        TweetWithUser(tweet, currentUserInfo)
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

    fun toggleFollowStatus() {
        _user.value?.let { currentUser ->
            viewModelScope.launch {
                val userId = currentUser.userId
                val isCurrentlyFollowing = currentUser.isFollowing

                val success = if (isCurrentlyFollowing) {
                    repository.unfollowUser(userId)
                } else {
                    repository.followUser(userId)
                }

                if (success) {
                    val updatedUser = currentUser.copy(
                        isFollowing = !isCurrentlyFollowing,
                        followerCount = if (isCurrentlyFollowing) currentUser.followerCount - 1 else currentUser.followerCount + 1
                    )
                    _user.postValue(updatedUser)
                }
            }
        }
    }

    fun toggleLikeStatus(tweetId: String) {
        _tweets.value?.find { it.tweet.tweetId == tweetId }?.let { tweetWithUser ->
            toggleLike(tweetWithUser.tweet) { updatedTweet ->
                val updatedList = _tweets.value?.map {
                    if (it.tweet.tweetId == tweetId) it.copy(tweet = updatedTweet) else it
                }
                _tweets.postValue(updatedList!!)
            }
        }
    }

    fun toggleRetweetStatus(tweetId: String) {
        _tweets.value?.find { it.tweet.tweetId == tweetId }?.let { tweetWithUser ->
            toggleRetweet(tweetWithUser.tweet) { updatedTweet ->
                val updatedList = _tweets.value?.map {
                    if (it.tweet.tweetId == tweetId) it.copy(tweet = updatedTweet) else it
                }
                _tweets.postValue(updatedList!!)
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