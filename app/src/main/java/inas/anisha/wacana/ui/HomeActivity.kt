package inas.anisha.wacana.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ActivityHomeBinding
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
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = title

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

        binding.tripList.button_add_trip.setOnClickListener {
            val intent = Intent(this@HomeActivity, NewTripActivity::class.java)
            startActivity(intent)
        }

        viewModel.tripEntity.observe(this, androidx.lifecycle.Observer { tripDataList ->
            (binding.tripList.item_list.adapter as TripRecyclerViewAdapter).updateList(
                viewModel.getTripItemVMList(
                    tripDataList
                ).reversed()
            )
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                viewModel.selectTrip(viewModel.tripItemViewModelList.size - 1)
                selectFirstItem(Handler(), binding.tripList.item_list)
            }
        })
    }

    private fun setupRecyclerView() {
        binding.tripList.item_list.adapter = TripRecyclerViewAdapter(
            this,
            this,
            mutableListOf(),
            object : TripRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val index = viewModel.tripItemViewModelList.size - position - 1
                    viewModel.selectTrip(index)
                    if (twoPane) {
                        val fragment = TripDetailFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(
                                    TripDetailFragment.ARG_ITEM_ID,
                                    viewModel.tripItemViewModelList[index].tripEntity
                                )
                            }
                        }
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.trip_detail_container, fragment)
                            .commit()
                    } else {
                        val intent =
                            Intent(this@HomeActivity, TripDetailActivity::class.java).apply {
                                putExtra(
                                    TripDetailFragment.ARG_ITEM_ID,
                                    viewModel.tripItemViewModelList[index].tripEntity
                                )
                            }
                        startActivity(intent)
                    }
                }
            }
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewModel.selectTrip(-1)
        } else if (viewModel.selectedTrip == -1) {
            viewModel.selectTrip(viewModel.tripItemViewModelList.size - 1)
        }
    }

    private fun selectFirstItem(handler: Handler, recyclerView: RecyclerView) {
        handler.post {
            if (!recyclerView.isComputingLayout) {
                // This will call first item by calling "performClick()" of view.
                (recyclerView.findViewHolderForLayoutPosition(0) as TripRecyclerViewAdapter.ViewHolder).itemView.performClick()
            } else {
                selectFirstItem(handler, recyclerView)
            }
        }
    }
}
