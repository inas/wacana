package inas.anisha.wacana.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ActivityHomeBinding
import inas.anisha.wacana.db.entity.TripEntity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.trip_list.*
import kotlinx.android.synthetic.main.trip_list.view.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [TripDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class HomeActivity : AppCompatActivity() {

    lateinit var viewModel: HomeViewModel
    lateinit var binding: ActivityHomeBinding

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home, null)
        setSupportActionBar(toolbar)
        toolbar.title = title

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        if (trip_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()

        button_add_trip.setOnClickListener {
            val intent = Intent(this@HomeActivity, NewTripActivity::class.java)
            startActivity(intent)
        }

        viewModel.tripEntity.observe(this, androidx.lifecycle.Observer { tripDataList ->
            (binding.tripList.item_list.adapter as TripRecyclerViewAdapter).updateList(
                viewModel.getTripItemVMList(
                    tripDataList
                )
            )
        })
    }

    private fun setupRecyclerView() {
        binding.tripList.item_list.adapter = TripRecyclerViewAdapter(
            mutableListOf(),
            object : TripRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(trip: TripEntity) {
                    if (twoPane) {
                        val fragment = TripDetailFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(TripDetailFragment.ARG_ITEM_ID, trip)
                            }
                        }
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.trip_detail_container, fragment)
                            .commit()
                    } else {
                        val intent =
                            Intent(this@HomeActivity, TripDetailActivity::class.java).apply {
                                putExtra(TripDetailFragment.ARG_ITEM_ID, trip)
                            }
                        startActivity(intent)
                    }
                }
            }
        )
    }

}
