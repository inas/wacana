package inas.anisha.wacana.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import inas.anisha.wacana.db.entity.DocumentEntity

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocument(document: DocumentEntity)

    @Delete
    fun deleteDocument(document: DocumentEntity)

    @Query("DELETE FROM document WHERE trip_id = :tripId")
    fun deleteAllDocuments(tripId: Long)

    @Query("SELECT * from document WHERE trip_id = :tripId")
    fun getAllDocuments(tripId: Long): LiveData<List<DocumentEntity>>

    @Query("SELECT * from document")
    fun getAllDocuments(): LiveData<List<DocumentEntity>>
}