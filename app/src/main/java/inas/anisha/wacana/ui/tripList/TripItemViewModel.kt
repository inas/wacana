package inas.anisha.wacana.ui.tripList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import inas.anisha.wacana.db.entity.TripEntity
import java.util.*

class TripItemViewModel : ViewModel() {

    var destination: String = ""
    var startDate: String = ""
    var endDate: String? = null
    var isSelected: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var tripEntity: TripEntity = TripEntity(0, "", Calendar.getInstance(), Calendar.getInstance())

    fun getDate(): String {
        var date = startDate + (if (endDate != null) " - $endDate" else "")
        return date
    }
}