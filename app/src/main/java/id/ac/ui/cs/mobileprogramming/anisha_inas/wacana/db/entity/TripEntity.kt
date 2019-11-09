package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "trip")
@Parcelize
data class TripEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "destination") val destination: String,
    @ColumnInfo(name = "start_date") var startDate: Calendar,
    @ColumnInfo(name = "end_date") var endDate: Calendar
) : Parcelable