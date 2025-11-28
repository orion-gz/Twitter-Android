package orion.app.twitter.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import orion.app.twitter.data.model.User
import orion.app.twitter.data.model.request.LoginRequest
import orion.app.twitter.data.model.request.SignUpRequest
import orion.app.twitter.data.network.TokenManager
import orion.app.twitter.data.repository.TwitterRepository

class AuthViewModel(
    private val repository: TwitterRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun signUp(username: String, displayName: String, email: String, password: String) {
        viewModelScope.launch {
            _authenticationState.value = AuthenticationState.LOADING
            val request = SignUpRequest(username, displayName, email, password)
            val result = repository.signup(request)
            if (result != null) {
                _authenticationState.value = AuthenticationState.SIGNUP_SUCCESSFUL
            } else {
                _authenticationState.value = AuthenticationState.SIGNUP_FAILED
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authenticationState.value = AuthenticationState.LOADING
            val request = LoginRequest(username, password)
            try {
                val result = repository.login(request)
                if (result != null) {
                    tokenManager.saveToken(result.token)
                    tokenManager.saveUserId(result.user.userId)

                    _user.value = result.user
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                } else {
                    _authenticationState.value = AuthenticationState.AUTHENTICATION_FAILED
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authenticationState.value = AuthenticationState.AUTHENTICATION_FAILED
            }
        }
    }
}

enum class AuthenticationState {
    LOADING,
    AUTHENTICATED,
    AUTHENTICATION_FAILED,
    UNAUTHENTICATED,
    SIGNUP_SUCCESSFUL,
    SIGNUP_FAILED
}