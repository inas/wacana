package inas.anisha.wacana

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import inas.anisha.wacana.apiProvider.response.WeatherResponse
import inas.anisha.wacana.apiProvider.retrofit.RetrofitRequest
import inas.anisha.wacana.apiProvider.retrofit.WeatherService
import inas.anisha.wacana.apiProvider.retrofit.WeatherService.Companion.API_KEY
import inas.anisha.wacana.db.AppDatabase
import inas.anisha.wacana.db.dao.DocumentDao
import inas.anisha.wacana.db.dao.TripDao
import inas.anisha.wacana.db.entity.DocumentEntity
import inas.anisha.wacana.db.entity.TripEntity
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Repository(application: Application) {

    companion object {
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

    init {
        val db = AppDatabase.getDatabase(application)
        tripDao = db.tripDao()
        documentDao = db.documentDao()
        weatherService =
            RetrofitRequest.getWeatherRetrofitInstance().create(WeatherService::class.java)
        weather = MutableLiveData()
    }

    fun getCurrentWeather(lat: String, lon: String): LiveData<WeatherResponse> {
        val data = MutableLiveData<WeatherResponse>()
        weatherService.getCurrentWeather(lat, lon, API_KEY)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    if (response.body() != null) {
                        Log.d("debugweather_retrofit", "onResponse response:: ${response.body()}")
                        weather.value = response.body()
                        data.value = response.body()
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    data.value = null
                }
            })
        val check = data.value
        return data
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