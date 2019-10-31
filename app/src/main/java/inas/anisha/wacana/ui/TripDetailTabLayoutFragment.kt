package inas.anisha.wacana.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.TripDetailTabLayoutBinding
import inas.anisha.wacana.db.entity.TripEntity
import inas.anisha.wacana.ui.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_trip_detail.*
import java.util.*


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [HomeActivity]
 * in two-pane mode (on tablets) or a [TripDetailActivity]
 * on handsets.
 */
class TripDetailTabLayoutFragment : Fragment() {

    lateinit var binding: TripDetailTabLayoutBinding
    lateinit var viewModel: TripDetailTabLayoutViewModel

    var sectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: TripEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TripDetailTabLayoutViewModel::class.java)

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
        item?.id?.let { viewModel.initViewModel(it) }

        requireActivity().let {
            requireFragmentManager().let { fm ->
                sectionsPagerAdapter =
                    SectionsPagerAdapter(it, childFragmentManager, viewModel.documentTabViewModel)
                val viewPager: ViewPager = binding.viewPager
                viewPager.adapter = sectionsPagerAdapter
                val tabs: TabLayout = binding.tabs
                tabs.setupWithViewPager(viewPager)
            }
        }


        viewModel.documents.observe(this, Observer {
            sectionsPagerAdapter?.run {
                viewModel.getDocumentUris(it).let {
                    val intent = Intent(DocumentTabFragment.DOCUMENT_BROADCAST)
                    intent.putStringArrayListExtra(
                        DocumentTabFragment.DOCUMENTS,
                        it as ArrayList<String>?
                    )
                    activity?.sendBroadcast(intent)
                }
            }
        })

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
