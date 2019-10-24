package inas.anisha.wacana.ui

import androidx.lifecycle.ViewModel
import inas.anisha.wacana.dataModel.TripDataModel

class TripItemViewModel : ViewModel() {

    var tripId: String = "id"
    var destination: String = ""
    var startDate: String = ""
    var endDate: String? = null
    var tripDetail: TripDataModel = TripDataModel()
    var isSelected: Boolean = false

    fun getDate(): String {
        return startDate
    }
}