package inas.anisha.wacana.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import inas.anisha.wacana.Repository
import inas.anisha.wacana.db.entity.DocumentEntity

class DocumentTabViewModel(application: Application) : AndroidViewModel(application) {
    var uriList: List<String> = mutableListOf()
    var documentList: LiveData<List<DocumentEntity>> = MutableLiveData()
    var tripId: Long = 0

    fun initViewModel(uris: List<String>?, id: Long?) {
//        uris?.let { uriList = it }
        id?.let { tripId = it }
        documentList = Repository.getInstance(getApplication()).getAllDocuments(tripId)
        documentList.value?.forEach {
            it.id
            it.tripId
        }
    }

    fun setUris(uris: List<String>) {
        uriList = uris
    }

    fun getUris(documents: List<DocumentEntity>): List<String> {
        return documents.map { it.uri }
    }
    fun addDocument(uri: Uri) {
        Repository.getInstance(getApplication()).insertDocument(
            DocumentEntity(0, uri.toString(), tripId)
        )
        var docs = Repository.getInstance(getApplication()).getAllDocuments().value
        docs?.let {
            it.forEach {
                it.id
                it.tripId
                it.uri
            }
        }
        docs = mutableListOf()
    }
}