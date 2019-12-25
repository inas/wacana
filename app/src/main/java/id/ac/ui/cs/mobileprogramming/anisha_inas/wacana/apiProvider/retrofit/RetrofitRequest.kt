package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitRequest {

    private const val WEATHER_BASE_URL = "http://api.openweathermap.org/"
    private var weatherRetrofit: Retrofit? = null

    val weatherRetrofitInstance: Retrofit
        get() {
            weatherRetrofit?.let { return it }
            val retrofit = Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            weatherRetrofit = retrofit
            return retrofit
        }
}
