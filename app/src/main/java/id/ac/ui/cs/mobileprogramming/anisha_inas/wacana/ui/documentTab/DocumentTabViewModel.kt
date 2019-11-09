package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.documentTab

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.Repository
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.DocumentEntity
import java.io.File

class DocumentTabViewModel(application: Application) : AndroidViewModel(application) {
    var uriList: List<String> = mutableListOf()
    var imageIdList: List<Long> = mutableListOf()
    var documentList: LiveData<List<DocumentEntity>> = MutableLiveData()
    var tripId: Long = 0

    fun initViewModel(id: Long?) {
        id?.let { tripId = it }
        documentList = Repository.getInstance(getApplication()).getAllDocuments(tripId)
    }

    fun getUris(documents: List<DocumentEntity>): List<String> {
        val validUris = mutableListOf<String>()
        val validIds = mutableListOf<Long>()
        val obsoleteDocuments = mutableListOf<DocumentEntity>()
        documents.forEach {
            if (File(it.filePath).exists()) {
                validUris.add(it.filePath)
                validIds.add(it.id)
            } else obsoleteDocuments.add(it)
        }
        Repository.getInstance(getApplication()).deleteDocuments(obsoleteDocuments)
        uriList = validUris
        imageIdList = validIds
        return uriList
    }

    fun addDocument(filePath: String) {
        Repository.getInstance(getApplication()).insertDocument(
            DocumentEntity(0, filePath, tripId)
        )
    }
}