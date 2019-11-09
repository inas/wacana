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
import kotlinx.android.synthetic.main.activity_trip_detail.*

class TripDetailTabLayoutFragment : Fragment() {

    lateinit var binding: TripDetailTabLayoutBinding

    private var sectionsPagerAdapter: SectionsPagerAdapter? = null

    private var item: TripEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_TRIP_ID)) {
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
                    SectionsPagerAdapter(
                        it,
                        childFragmentManager,
                        item?.id ?: 0
                    )
                val viewPager: ViewPager = binding.viewPager
                viewPager.adapter = sectionsPagerAdapter
                val tabs: TabLayout = binding.tabs
                tabs.setupWithViewPager(viewPager)
            }
        }

        return binding.root
    }

    companion object {
        const val ARG_TRIP_ID = "TRIP_ID"
    }
}
