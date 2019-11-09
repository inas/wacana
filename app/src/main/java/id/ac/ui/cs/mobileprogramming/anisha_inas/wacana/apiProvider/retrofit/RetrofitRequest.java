package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {

    private static final String WEATHER_BASE_URL = "http://api.openweathermap.org/";
    private static Retrofit weatherRetrofit;

    public static Retrofit getWeatherRetrofitInstance() {
        if (weatherRetrofit == null) {
            weatherRetrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(WEATHER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return weatherRetrofit;
    }
}
