package inas.anisha.wacana.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import inas.anisha.wacana.Repository
import inas.anisha.wacana.db.entity.TripEntity
import inas.anisha.wacana.ui.tripList.TripItemViewModel
import org.apache.commons.lang3.time.DateUtils
import java.text.SimpleDateFormat

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var selectedTrip: Int = -1
    var tripItemViewModelList: List<TripItemViewModel> = mutableListOf()
    var tripEntity: LiveData<List<TripEntity>> =
        Repository.getInstance(getApplication()).getAllTrip()
    var locationData = LocationLiveData(application)

    fun getTripItemVMList(tripEntityList: List<TripEntity>): List<TripItemViewModel> {
        tripItemViewModelList = tripEntityList.map {
            TripItemViewModel().apply {
                destination = it.destination
                startDate = SimpleDateFormat("dd MMM yyyy").format(it.startDate.time)
                endDate = if (DateUtils.isSameDay(it.startDate, it.endDate)) null else
                    SimpleDateFormat("dd MMM yyyy").format(it.endDate.time)
                isSelected.value = false
                tripEntity = it
            }
        }
        return tripItemViewModelList
    }

    fun selectTrip(newTripIndex: Int) {
        if (selectedTrip >= 0 && selectedTrip < tripItemViewModelList.size)
            tripItemViewModelList[selectedTrip].isSelected.value = false
        selectedTrip = newTripIndex
        if (newTripIndex >= 0 && newTripIndex < tripItemViewModelList.size)
            tripItemViewModelList[newTripIndex].isSelected.value = true
    }
}