package orion.app.twitter.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import orion.app.twitter.data.model.User
import orion.app.twitter.data.repository.TwitterRepository

class SearchViewModel(private val repository: TwitterRepository) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _users.postValue(emptyList())
            return
        }

        viewModelScope.launch {
            val result = repository.searchUsers(query)
            _users.postValue(result ?: emptyList())
        }
    }
}
