package inas.anisha.wacana.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import inas.anisha.wacana.Repository
import inas.anisha.wacana.db.entity.DocumentEntity

class TripDetailTabLayoutViewModel(application: Application) : AndroidViewModel(application) {
    var documents: LiveData<List<DocumentEntity>> = MutableLiveData()
    var documentTabViewModel: DocumentTabViewModel = DocumentTabViewModel(getApplication())

    fun initViewModel(tripId: Long) {
        documents = Repository.getInstance(getApplication()).getAllDocuments(tripId)
        documentTabViewModel = DocumentTabViewModel(getApplication()).apply {
            uriList = getDocumentUris(documents.value ?: mutableListOf())
            this.tripId = tripId
        }
    }

    fun getDocumentUris(documents: List<DocumentEntity>): List<String> {
        return documents.map { it.uri }
    }
}