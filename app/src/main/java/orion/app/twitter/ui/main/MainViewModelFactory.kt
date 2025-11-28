package orion.app.twitter.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import orion.app.twitter.data.network.RetrofitInstance
import orion.app.twitter.data.remote.TwitterApiService
import orion.app.twitter.data.repository.TwitterRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val api = RetrofitInstance.getApi(context)
            val repository = TwitterRepository(api)
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
