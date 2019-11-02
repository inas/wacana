package inas.anisha.wacana.ui.newTrip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import inas.anisha.wacana.Repository
import inas.anisha.wacana.db.entity.TripEntity
import java.util.*

class NewTripViewModel(application: Application) : AndroidViewModel(application) {
    var destination: String = ""
    var startDate: MutableLiveData<Calendar> =
        MutableLiveData<Calendar>().apply { value = Calendar.getInstance() }
    var endDate: MutableLiveData<Calendar> =
        MutableLiveData<Calendar>().apply { value = Calendar.getInstance() }

    fun addTrip() {
        startDate.value?.let { start ->
            endDate.value?.let { end ->
                Repository.getInstance(getApplication())
                    .addTrip(TripEntity(0, destination, start, end))
            }
        }
    }
}