package inas.anisha.wacana.ui

import androidx.lifecycle.ViewModel
import inas.anisha.wacana.db.entity.TripEntity
import java.util.*

class TripItemViewModel : ViewModel() {

    var tripId: String = "id"
    var destination: String = ""
    var startDate: String = ""
    var endDate: String? = null
    var isSelected: Boolean = false
    var tripEntity: TripEntity = TripEntity(0, "", Calendar.getInstance(), Calendar.getInstance())

    fun getDate(): String {
        return startDate + (if (endDate != null) " - $endDate" else "")
    }
}