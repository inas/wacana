package inas.anisha.wacana.ui.tripDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.TripDetailTabLayoutBinding
import inas.anisha.wacana.db.entity.TripEntity
import inas.anisha.wacana.ui.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_trip_detail.*


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [HomeActivity]
 * in two-pane mode (on tablets) or a [TripDetailActivity]
 * on handsets.
 */
class TripDetailTabLayoutFragment : Fragment() {

    lateinit var binding: TripDetailTabLayoutBinding

    var sectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: TripEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_TRIP_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = it.getParcelable(ARG_TRIP_ID) as TripEntity
                activity?.toolbar_layout?.title = item?.destination
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.trip_detail_tab_layout, container, false)

        requireActivity().let {
            requireFragmentManager().let { fm ->
                sectionsPagerAdapter =
                    SectionsPagerAdapter(it, childFragmentManager, item?.id ?: 0)
                val viewPager: ViewPager = binding.viewPager
                viewPager.adapter = sectionsPagerAdapter
                val tabs: TabLayout = binding.tabs
                tabs.setupWithViewPager(viewPager)
            }
        }

        return binding.root
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_TRIP_ID = "trip_id"
    }
}
