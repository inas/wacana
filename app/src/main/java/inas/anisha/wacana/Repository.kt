package inas.anisha.wacana

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import inas.anisha.wacana.db.AppDatabase
import inas.anisha.wacana.db.dao.DocumentDao
import inas.anisha.wacana.db.dao.TripDao
import inas.anisha.wacana.db.entity.DocumentEntity
import inas.anisha.wacana.db.entity.TripEntity
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


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
    var documentList: LiveData<List<DocumentEntity>>

    init {
        val db = AppDatabase.getDatabase(application)
        tripDao = db.tripDao()
        documentDao = db.documentDao()
        documentList = documentDao.getAllDocuments()
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
        val document = getAllDocuments()
        val docs = document
    }

    fun deleteDocument(document: DocumentEntity) {
        documentDao.deleteDocument(document)
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