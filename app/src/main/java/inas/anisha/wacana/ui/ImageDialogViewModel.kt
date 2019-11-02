package inas.anisha.wacana.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import inas.anisha.wacana.Repository

class ImageDialogViewModel(application: Application) : AndroidViewModel(application) {
    var imageFilePath: String = ""
    var imageId: Long = 0

    fun initViewModel(id: Long, filePath: String) {
        imageId = id
        imageFilePath = filePath
    }

    fun removeImage() {
        Repository.getInstance(getApplication())
            .deleteDocument(imageId, imageFilePath)
    }
}