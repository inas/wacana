package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.retrofit

import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather?")
    fun getCurrentWeather(
        @Query("lat") lat: String, @Query("lon") lon: String
        , @Query("APPID") app_id: String
    ): Call<WeatherResponse>
}