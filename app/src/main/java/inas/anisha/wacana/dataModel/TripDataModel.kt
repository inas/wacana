package inas.anisha.wacana.dataModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TripDataModel(
    var destination: String = "",
    var startDate: Calendar = Calendar.getInstance(),
    var endDate: Calendar? = null
) : Parcelable