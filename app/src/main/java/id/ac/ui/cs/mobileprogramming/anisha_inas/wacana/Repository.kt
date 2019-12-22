package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.response.Main
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.response.Weather
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.response.WeatherResponse
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.retrofit.RetrofitRequest
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.retrofit.WeatherService
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.apiProvider.retrofit.WeatherService.Companion.API_KEY
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.AppDatabase
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.dao.DocumentDao
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.dao.ItemDao
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.dao.TripDao
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.DocumentEntity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.ItemEntity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.TripEntity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.preferences.AppPreference
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.workers.NotificationWorker
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.workers.WeatherWorker
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
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
    var itemDao: ItemDao
    var weatherService: WeatherService
    var weather: MutableLiveData<WeatherResponse>
    var sharedPreference: AppPreference
    var workManager: WorkManager

    init {
        val db = AppDatabase.getDatabase(application)
        tripDao = db.tripDao()
        documentDao = db.documentDao()
        itemDao = db.itemDao()
        weatherService =
            RetrofitRequest.getWeatherRetrofitInstance().create(WeatherService::class.java)
        weather = MutableLiveData()
        sharedPreference = AppPreference.getInstance(application)
        workManager = WorkManager.getInstance(application)
    }

    fun getLatitude() = sharedPreference.latitude
    fun getLongitude() = sharedPreference.longitude

    fun saveLocationCoordinates(lat: String, lon: String) {
        sharedPreference.saveLocationCoordinates(lat, lon)
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
        return Observable.fromCallable { tripDao.getAllTrip() }
            .subscribeOn(Schedulers.io()).blockingSingle()
    }

    fun addTrip(trip: TripEntity) {
        Observable.fromCallable { tripDao.insert(trip) }
            .subscribeOn(Schedulers.io())
            .subscribe({ id ->
                tripDao.getTripById(id).let {
                    scheduleNotification(it.destination, it.startDate, id)
                }
            })
    }

    fun deleteTrip(trip: TripEntity) {
        Observable.fromCallable { tripDao.deleteTrip(trip) }.subscribeOn(Schedulers.io())
            .subscribe({
                workManager.cancelAllWorkByTag(NotificationWorker.TRIP_NOTIFICATION + trip.id)
            })
    }

    fun clearTrip() {
        Observable.fromCallable { tripDao.deleteAll() }.subscribeOn(Schedulers.io()).subscribe()
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

    fun getAllItems(tripId: Long): LiveData<List<ItemEntity>> {
        return Observable.fromCallable { itemDao.getAllItems(tripId) }
            .subscribeOn(Schedulers.io()).blockingSingle()
    }

    fun insertItems(items: MutableList<ItemEntity>, tripId: Long) {
        val deleteAllCompletable = Completable.fromAction { itemDao.deleteItems(tripId) }
        val insertAllCompletable =
            Completable.fromAction { itemDao.insertItem(*items.toTypedArray()) }

        deleteAllCompletable
            .andThen(insertAllCompletable)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun scheduleNotification(destination: String, startDate: Calendar, id: Long) {
        val inputData = Data.Builder().apply {
            putString(NotificationWorker.DESTINATION, destination)
            putLong(NotificationWorker.REMINDER_TIME, startDate.timeInMillis)
        }.build()
        val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(calculateDelay(startDate))
            .setInputData(inputData)
            .addTag(NotificationWorker.TRIP_NOTIFICATION + id.toString())
            .build()

        workManager.enqueue(notificationWork)
    }

    private fun calculateDelay(date: Calendar): Duration {
        val dueDate = Calendar.getInstance()
        date.let {
            dueDate.apply {
                set(Calendar.YEAR, it.get(Calendar.YEAR))
                set(Calendar.MONTH, it.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, it.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 12)
            }
        }
        return Duration.ofMillis(dueDate.timeInMillis - System.currentTimeMillis())
    }
}