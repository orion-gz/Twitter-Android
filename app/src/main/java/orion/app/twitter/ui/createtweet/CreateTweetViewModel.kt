package orion.app.twitter.ui.createtweet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import orion.app.twitter.data.model.Tweet
import orion.app.twitter.data.repository.TwitterRepository

class CreateTweetViewModel(private val repository: TwitterRepository) : ViewModel() {

    private val _tweetPosted = MutableLiveData<Boolean>()
    val tweetPosted: LiveData<Boolean> = _tweetPosted

    fun postTweet(content: String) {
        viewModelScope.launch {
            val userId = "1"
            val newTweet = Tweet(
                tweetId = "",
                userId = userId,
                content = content,
                createdAt = "",
                parentTweetId = null,
                quotedTweetId = null,
                replyCount = 0,
                likeCount = 0,
                retweetCount = 0
            )
            val result = repository.createTweet(newTweet)
            _tweetPosted.postValue(result != null)
        }
    }
}
