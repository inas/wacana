package inas.anisha.wacana.ui.tripDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import inas.anisha.wacana.R
import inas.anisha.wacana.Repository
import inas.anisha.wacana.db.entity.TripEntity
import org.apache.commons.lang3.time.DateUtils
import java.text.SimpleDateFormat

class TripDetailViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var tripEntity: TripEntity

    var destination: String = ""
    var date: String = ""

    fun initViewModel(trip: TripEntity) {
        tripEntity = trip
        destination = trip.destination
        val startDate = SimpleDateFormat(
            getApplication<Application>()
                .resources.getString(R.string.common_date_format_dd_mmm_yyyy)
        ).format(trip.startDate.time)
        val endDate =
            if (trip.endDate == null || DateUtils.isSameDay(trip.startDate, trip.endDate)) null else
                SimpleDateFormat(
                    getApplication<Application>()
                        .resources.getString(R.string.common_date_format_dd_mmm_yyyy)
                ).format(trip.endDate.time)
        date = startDate + (if (endDate != null) " - $endDate" else "")
    }

    fun deleteTrip() {
        Repository.getInstance(getApplication()).deleteTrip(tripEntity)
    }
}