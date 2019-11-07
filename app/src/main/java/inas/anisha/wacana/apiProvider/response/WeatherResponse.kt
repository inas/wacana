package inas.anisha.wacana.apiProvider.response

import com.google.gson.annotations.SerializedName
import java.util.*

class WeatherResponse {
    @SerializedName("coord")
    var coord: Coord? = null
    @SerializedName("weather")
    var weather = ArrayList<Weather>()
    @SerializedName("main")
    var main: Main? = null
    @SerializedName("name")
    var name: String? = null
}

class Coord {
    @SerializedName("lon")
    var lon: Double? = null
    @SerializedName("lat")
    var lat: Double? = null
}

class Weather {
    @SerializedName("main")
    var main: String? = null
}

class Main {
    @SerializedName("temp")
    var temp: Float? = 0.toFloat()
}