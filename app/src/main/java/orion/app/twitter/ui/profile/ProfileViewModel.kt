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
        viewModelScope.launch {
            repository.getUser(userId)?.let { user ->
                _user.postValue(user)
                loadTabContent(0)
            }
        }
    }

    fun loadTabContent(tabIndex: Int) {
        val userId = currentUserId ?: return
        val user = _user.value ?: return

        viewModelScope.launch {
            val tweetList = when (tabIndex) {
                0 -> repository.getTweetsForUser(userId)
                1 -> repository.getRepliesForUser(userId)
                2 -> repository.getLikesForUser(userId)
                else -> emptyList()
            }

            tweetList?.let { list ->
                val tweetsWithUser = list.map { tweet ->
                    // Likes 탭일 경우, 작성자는 '내가' 아니라 '그 글을 쓴 사람'이어야 합니다.
                    // 하지만 현재 API 구조상 리스트에는 트윗 정보만 오고 작성자 정보(User)가 포함되지 않을 수 있습니다.
                    // 일단은 프로필 주인(user) 정보를 그대로 쓰되,
                    // *완벽하게 하려면 백엔드에서 Tweet 내부에 작성자 정보를 포함해서 보내줘야 합니다.*
                    // 여기서는 편의상 프로필 주인으로 매핑합니다. (Likes 탭에서는 작성자가 다르게 보일 수 있음 주의)
                    TweetWithUser(tweet, user)
                }
                _tweets.postValue(tweetsWithUser)
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
                val updatedList = _tweets.value?.map { if (it.tweet.tweetId == tweetId) it.copy(tweet = updatedTweet) else it }
                _tweets.postValue(updatedList!!)
            }
        }
    }

    fun toggleRetweetStatus(tweetId: String) {
        _tweets.value?.find { it.tweet.tweetId == tweetId }?.let { tweetWithUser ->
            toggleRetweet(tweetWithUser.tweet) { updatedTweet ->
                val updatedList = _tweets.value?.map { if (it.tweet.tweetId == tweetId) it.copy(tweet = updatedTweet) else it }
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
