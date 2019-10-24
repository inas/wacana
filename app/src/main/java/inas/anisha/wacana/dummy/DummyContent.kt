package inas.anisha.wacana.dummy

import inas.anisha.wacana.dataModel.TripDataModel
import inas.anisha.wacana.ui.TripItemViewModel
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<TripItemViewModel> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, TripItemViewModel> = HashMap()

    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: TripItemViewModel) {
        ITEMS.add(item)
        ITEM_MAP[item.tripId] = item
    }

    private fun createDummyItem(position: Int): TripItemViewModel {
        return TripItemViewModel().apply {
            tripId = "trip_$position"
            destination = "Singapore"
            startDate = "Sep 2 2019"
            endDate = "Sep 5 2019"
            tripDetail = TripDataModel("Singapore", Calendar.getInstance())
        }
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class DummyItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}
