package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.tripList

import androidx.lifecycle.ViewModel
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.TripEntity
import java.util.*

class TripItemViewModel : ViewModel() {

    var destination: String = ""
    var startDate: String = ""
    var endDate: String? = null
    var tripEntity: TripEntity = TripEntity(0, "", Calendar.getInstance(), Calendar.getInstance())

    fun getDate() = startDate + (if (endDate != null) " - $endDate" else "")
}