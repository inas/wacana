package inas.anisha.wacana.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreference(mContext: Context) {
    private val mSharedPreferences: SharedPreferences =
        mContext.getSharedPreferences("wacana_data", Context.MODE_PRIVATE)

    val location: String? = mSharedPreferences.getString(LOCATION, null)

    val weather: String? = mSharedPreferences.getString(WEATHER, null)

    val temperature: String? = mSharedPreferences.getString(TEMPERATURE, null)

    fun saveLocation(location: String?) {
        location?.let { setValue(LOCATION, it) }
    }

    fun saveWeather(weather: String?) {
        weather?.let { setValue(WEATHER, it) }
    }

    fun saveTemperature(temperature: String?) {
        temperature?.let { setValue(TEMPERATURE, it) }
    }

    fun removeKey(key: String) {
        this.mSharedPreferences.edit().remove(key).apply()
    }

    private fun setValue(key: String, value: String) {
        this.mSharedPreferences.edit().putString(key, value).apply()
    }

    companion object {
        private const val LOCATION = "location"
        private const val WEATHER = "weather"
        private const val TEMPERATURE = "temperature"

        @Volatile
        private var instance: AppPreference? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: AppPreference(context.applicationContext)
            }

    }
}
