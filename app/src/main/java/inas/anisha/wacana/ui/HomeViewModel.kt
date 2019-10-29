package inas.anisha.wacana.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import inas.anisha.wacana.Repository
import inas.anisha.wacana.db.entity.TripEntity
import org.apache.commons.lang3.time.DateUtils
import java.text.SimpleDateFormat

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    var tripEntity: LiveData<List<TripEntity>> =
        Repository.getInstance(getApplication()).getAllTrip()

    fun getTripItemVMList(tripEntityList: List<TripEntity>): List<TripItemViewModel> {
        val tripItemViewModelList = tripEntityList.map {
            TripItemViewModel().apply {
                destination = it.destination
                startDate = SimpleDateFormat("dd MMM yyyy").format(it.startDate.time)
                endDate = if (DateUtils.isSameDay(it.startDate, it.endDate)) null else
                    SimpleDateFormat("dd MMM yyyy").format(it.endDate.time)
                isSelected = false
                tripEntity = it
            }
        }
        if (tripItemViewModelList.size > 0) tripItemViewModelList[0].isSelected = true
        return tripItemViewModelList
    }
}