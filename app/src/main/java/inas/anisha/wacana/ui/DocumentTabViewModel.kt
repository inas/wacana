package inas.anisha.wacana.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import inas.anisha.wacana.Repository
import inas.anisha.wacana.db.entity.DocumentEntity

class DocumentTabViewModel(application: Application) : AndroidViewModel(application) {
    var uriList: List<String> = mutableListOf()
    var tripId: Long = 0

    fun initViewModel(uris: List<String>?, id: Long?) {
        uris?.let { uriList = it }
        id?.let { tripId = it }
    }

    fun setUris(uris: List<String>) {
        uriList = uris
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