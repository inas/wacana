package inas.anisha.wacana

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import inas.anisha.wacana.db.AppDatabase
import inas.anisha.wacana.db.dao.TripDao
import inas.anisha.wacana.db.entity.TripEntity

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

    init {
        val db = AppDatabase.getDatabase(application)
        tripDao = db.tripDao()
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
}