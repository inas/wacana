package inas.anisha.wacana.apiProvider.response

import com.google.gson.annotations.SerializedName
import java.util.*

class WeatherResponse {
    @SerializedName("weather")
    var weather = ArrayList<Weather>()
    @SerializedName("main")
    var main: Main? = null
    @SerializedName("name")
    var name: String? = null
}

class Weather {
    @SerializedName("main")
    var main: String? = null
}

class Main {
    @SerializedName("temp")
    var temp: Float? = 0.toFloat()
}