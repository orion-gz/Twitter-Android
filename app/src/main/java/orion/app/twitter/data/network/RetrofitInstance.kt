package orion.app.twitter.data.network

import android.content.Context
import okhttp3.OkHttpClient
import orion.app.twitter.data.remote.TwitterApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private var api: TwitterApiService? = null

    fun getApi(context: Context): TwitterApiService {
        if (api == null) {
            val tokenManager = TokenManager(context)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            api = retrofit.create(TwitterApiService::class.java)
        }
        return api!!
    }
}