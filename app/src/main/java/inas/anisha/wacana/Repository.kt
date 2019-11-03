package inas.anisha.wacana

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import inas.anisha.wacana.apiProvider.response.Main
import inas.anisha.wacana.apiProvider.response.Weather
import inas.anisha.wacana.apiProvider.response.WeatherResponse
import inas.anisha.wacana.apiProvider.retrofit.RetrofitRequest
import inas.anisha.wacana.apiProvider.retrofit.WeatherService
import inas.anisha.wacana.apiProvider.retrofit.WeatherService.Companion.API_KEY
import inas.anisha.wacana.apiProvider.retrofit.WeatherWorker
import inas.anisha.wacana.db.AppDatabase
import inas.anisha.wacana.db.dao.DocumentDao
import inas.anisha.wacana.db.dao.TripDao
import inas.anisha.wacana.db.entity.DocumentEntity
import inas.anisha.wacana.db.entity.TripEntity
import inas.anisha.wacana.preferences.AppPreference
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Repository(application: Application) {

    companion object {
        const val LONGITUDE = "LONGITUDE"
        const val LATITUDE = "LATITUDE"
        const val WEATHER_JOB = "WEATHER_JOB"

        // For Singleton instantiation
        @Volatile
        private var instance: Repository? = null

        fun getInstance(app: Application) =
            instance ?: synchronized(this) {
                instance ?: Repository(app).also { instance = it }
            }
    }

    var tripDao: TripDao
    var documentDao: DocumentDao
    var weatherService: WeatherService
    var weather: MutableLiveData<WeatherResponse>
    var sharedPreference: AppPreference
    var workManager: WorkManager

    init {
        val db = AppDatabase.getDatabase(application)
        tripDao = db.tripDao()
        documentDao = db.documentDao()
        weatherService =
            RetrofitRequest.getWeatherRetrofitInstance().create(WeatherService::class.java)
        weather = MutableLiveData()
        sharedPreference = AppPreference.getInstance(application)
        workManager = WorkManager.getInstance(application)
    }

    fun getCurrentWeather(lat: String, lon: String) {
        weatherService.getCurrentWeather(lat, lon, API_KEY)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    response.body()?.let {
                        weather.value = response.body()
                        weather.value?.let {
                            with(sharedPreference) {
                                saveLocation(it.name)
                                saveWeather(it.weather[0].main)
                                saveTemperature(it.main?.temp.toString())
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    queueGetWeatherCall(lat, lon)

                    sharedPreference.let {
                        weather.value = WeatherResponse().apply {
                            name = it.location
                            main = Main().apply { temp = it.temperature?.toFloat() }
                            weather = mutableListOf(Weather().apply {
                                main = it.weather
                            }) as ArrayList<Weather>
                        }
                    }
                }
            })
    }

    private fun queueGetWeatherCall(lat: String, lon: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val job = OneTimeWorkRequest.Builder(WeatherWorker::class.java).apply {
            val data = Data.Builder()
            data.putString(LONGITUDE, lon)
            data.putString(LATITUDE, lat)
            setInputData(data.build())
            addTag(WEATHER_JOB)
        }.setConstraints(constraints).build()

        workManager.beginUniqueWork(WEATHER_JOB, ExistingWorkPolicy.REPLACE, job).enqueue()
    }

    fun getAllTrip(): LiveData<List<TripEntity>> {
        return tripDao.getAllTrip()
    }

    fun addTrip(trip: TripEntity) {
        InsertTripAsyncTask(tripDao).execute(trip)
    }

    fun deleteTrip(trip: TripEntity) {
        deleteTripAsyncTask(tripDao).execute(trip)
    }

    fun clearTrip() {
        clearTripAsyncTask(tripDao).execute()
    }

    fun getAllDocuments(): LiveData<List<DocumentEntity>> {
        return Observable.fromCallable { documentDao.getAllDocuments() }
            .subscribeOn(Schedulers.io()).blockingSingle()
    }

    fun getAllDocuments(tripId: Long): LiveData<List<DocumentEntity>> {
        return Observable.fromCallable { documentDao.getAllDocuments(tripId) }
            .subscribeOn(Schedulers.io()).blockingSingle()
    }

    fun insertDocument(document: DocumentEntity) {
        Observable.fromCallable { documentDao.insertDocument(document) }
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun deleteDocument(imageId: Long, filePath: String) {
        Observable.fromCallable { documentDao.deleteDocument(imageId, filePath) }
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun deleteDocuments(document: List<DocumentEntity>) {
        Observable.fromCallable { documentDao.deleteDocuments(*document.toTypedArray()) }
            .subscribeOn(Schedulers.io()).subscribe()
    }

    private class InsertTripAsyncTask internal constructor(private val mAsyncTripDao: TripDao) :
        AsyncTask<TripEntity, Void, Void>() {
        override fun doInBackground(vararg params: TripEntity): Void? {
            mAsyncTripDao.insert(params[0])
            return null
        }
    }

    private class deleteTripAsyncTask internal constructor(private val mAsyncTripDao: TripDao) :
        AsyncTask<TripEntity, Void, Void>() {
        override fun doInBackground(vararg params: TripEntity): Void? {
            mAsyncTripDao.deleteTrip(params[0])
            return null
        }
    }

    private class clearTripAsyncTask internal constructor(private val mAsyncTripDao: TripDao) :
        AsyncTask<TripEntity, Void, Void>() {
        override fun doInBackground(vararg params: TripEntity): Void? {
            mAsyncTripDao.deleteAll()
            return null
        }
    }

    private class getDocumentsAsyncTask internal constructor(
        private val mAsyncDocumentDao: DocumentDao,
        private val tripId: Long
    ) :
        AsyncTask<Long, Void, LiveData<List<DocumentEntity>>>() {
        override fun doInBackground(vararg params: Long?): LiveData<List<DocumentEntity>> {

            return mAsyncDocumentDao.getAllDocuments(tripId)
        }
    }
}