package inas.anisha.wacana.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "item", foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["trip_id"]
    )]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "item_id") val id: Long,
    @ColumnInfo(name = "item") val item: String,
    @ColumnInfo(name = "isSelected") val isSelected: Boolean,
    @ColumnInfo(name = "trip_id") val tripId: Long
)