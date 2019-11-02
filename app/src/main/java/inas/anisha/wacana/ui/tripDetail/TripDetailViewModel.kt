package inas.anisha.wacana.ui.tripDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import inas.anisha.wacana.db.entity.TripEntity
import org.apache.commons.lang3.time.DateUtils
import java.text.SimpleDateFormat

class TripDetailViewModel(application: Application) : AndroidViewModel(application) {
    var destination: String = ""
    var date: String = ""

    fun initViewModel(trip: TripEntity) {
        destination = trip.destination
        val startDate = SimpleDateFormat("dd MMM yyyy").format(trip.startDate.time)
        val endDate =
            if (trip.endDate == null || DateUtils.isSameDay(trip.startDate, trip.endDate)) null else
                SimpleDateFormat("dd MMM yyyy").format(trip.endDate.time)
        date = startDate + (if (endDate != null) " - $endDate" else "")
    }
}