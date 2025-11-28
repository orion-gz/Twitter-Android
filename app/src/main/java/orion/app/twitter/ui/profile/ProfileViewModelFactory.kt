package orion.app.twitter.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import orion.app.twitter.data.network.RetrofitInstance
import orion.app.twitter.data.repository.TwitterRepository

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val api = RetrofitInstance.getApi(context)
            val repository = TwitterRepository(api)
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}