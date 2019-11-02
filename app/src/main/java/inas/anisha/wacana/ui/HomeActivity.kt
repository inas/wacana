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

class HomeActivity : AppCompatActivity() {

    lateinit var viewModel: HomeViewModel
    lateinit var binding: ActivityHomeBinding

    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home, null)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = title

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

    }

    override fun onStart() {
        super.onStart()
        twoPane = trip_detail_container != null
        initViews()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewModel.selectTrip(-1)
        } else if (viewModel.selectedTrip == -1) {
            viewModel.selectTrip(viewModel.tripItemViewModelList.size - 1)
        }
    }

    private fun initViews() {
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
                    openTripDetail(index)
                }
            }
        )
    }

    private fun selectFirstItem(handler: Handler, recyclerView: RecyclerView) {
        handler.post {
            if (!recyclerView.isComputingLayout) {
                // This will call first item by calling "performClick()" of view.
                recyclerView.findViewHolderForLayoutPosition(0)
                    ?.let { it as TripRecyclerViewAdapter.ViewHolder }
                    ?.itemView?.performClick()
            } else {
                selectFirstItem(handler, recyclerView)
            }
        }
    }

    private fun openTripDetail(tripIndex: Int) {
        if (twoPane) {
            val fragment = TripDetailTabLayoutFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(
                        TripDetailTabLayoutFragment.ARG_TRIP_ID,
                        this@HomeActivity.viewModel.tripItemViewModelList[tripIndex].tripEntity
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
                        TripDetailTabLayoutFragment.ARG_TRIP_ID,
                        viewModel.tripItemViewModelList[tripIndex].tripEntity
                    )
                }
            startActivity(intent)
        }
    }
}
