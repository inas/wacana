package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "document", foreignKeys = [ForeignKey(
        onDelete = ForeignKey.CASCADE,
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["trip_id"]
    )]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "document_id") val id: Long,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "trip_id") val tripId: Long
)