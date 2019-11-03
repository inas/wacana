package inas.anisha.wacana.apiProvider.retrofit

import inas.anisha.wacana.apiProvider.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    companion object {
        const val API_KEY = "0d41bfc96c89d3302775ec6fa537e07e"
    }

    @GET("data/2.5/weather?")
    fun getCurrentWeather(
        @Query("lat") lat: String, @Query("lon") lon: String
        , @Query("APPID") app_id: String
    ): Call<WeatherResponse>
}