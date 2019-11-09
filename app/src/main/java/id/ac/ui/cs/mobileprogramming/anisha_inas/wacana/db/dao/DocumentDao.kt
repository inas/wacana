package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.DocumentEntity

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocument(document: DocumentEntity)

    @Delete
    fun deleteDocuments(vararg document: DocumentEntity)

    @Query("DELETE FROM document WHERE document_id = :imageId AND file_path = :filePath")
    fun deleteDocument(imageId: Long, filePath: String)

    @Query("DELETE FROM document WHERE trip_id = :tripId")
    fun deleteAllDocuments(tripId: Long)

    @Query("SELECT * from document WHERE trip_id = :tripId")
    fun getAllDocuments(tripId: Long): LiveData<List<DocumentEntity>>

    @Query("SELECT * from document")
    fun getAllDocuments(): LiveData<List<DocumentEntity>>
}