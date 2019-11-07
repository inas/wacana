package inas.anisha.wacana.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "item", foreignKeys = [ForeignKey(
        onDelete = CASCADE,
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["trip_id"]
    )]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "item_id") val id: Long,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "is_selected") val isSelected: Boolean,
    @ColumnInfo(name = "trip_id") val tripId: Long
)