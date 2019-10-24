package inas.anisha.wacana.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import inas.anisha.wacana.R
import inas.anisha.wacana.dataModel.TripDataModel
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
import java.util.*

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [HomeActivity]
 * in two-pane mode (on tablets) or a [TripDetailActivity]
 * on handsets.
 */
class TripDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: TripDataModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                it.getParcelable<TripDataModel>(ARG_ITEM_ID)
                item = it.getParcelable(ARG_ITEM_ID) as TripDataModel
                activity?.toolbar_layout?.title = item?.destination
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            val year = it.startDate.get(Calendar.YEAR)
            val month =
                it.startDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
            val day = it.startDate.get(Calendar.DAY_OF_MONTH)
            rootView.trip_detail_text_view.text = "$day $month $year"
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
