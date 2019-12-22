package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.home

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.Repository
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.databinding.ActivityHomeBinding
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.newTrip.NewTripActivity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.tripDetail.TripDetailActivity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.tripDetail.TripDetailTabLayoutFragment
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.tripList.TripRecyclerViewAdapter
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.util.GpsUtil
import kotlinx.android.synthetic.main.trip_list.*

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var gpsReceiver: BroadcastReceiver

    private var twoPane: Boolean = false
    private var isGPSEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home, null)
        setSupportActionBar(binding.homeToolbar)
        binding.homeToolbar.title = title

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.initViewModel()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (savedInstanceState == null) {
            if (!isPermissionsGranted()) {
                requestPermission()
            } else {
                GpsUtil(this).turnGPSOn(object : GpsUtil.OnGpsListener {
                    override fun gpsStatus(isGPSEnabled: Boolean) {
                        this@HomeActivity.isGPSEnabled = isGPSEnabled
                    }
                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isPermissionsGranted() && isGPSEnabled) startLocationUpdate()

        twoPane = trip_detail_container != null

        initViews()

        viewModel.weather.observe(this, Observer {
            viewModel.updateWeather(it)
            if (it.main?.temp == null || it.name == null || it.weather[0].main == null) {
                trip_list_layout_weather.visibility = View.GONE
            } else {
                trip_list_layout_weather.visibility = View.VISIBLE
                trip_list_text_view_location.text = viewModel.locationName
                trip_list_text_view_temperature.text = viewModel.temperature
                trip_list_text_view_weather.text = viewModel.weatherDescription
            }
        })

        gpsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent.action?.let {
                    if (it.matches(LocationManager.PROVIDERS_CHANGED_ACTION.toRegex())) {
                        val locationManager =
                            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            isGPSEnabled = true
                            if (isPermissionsGranted()) startLocationUpdate()
                        } else {
                            isGPSEnabled = false
                        }
                    }
                }
            }
        }
        registerReceiver(gpsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewModel.selectTrip(-1)
        } else if (viewModel.selectedTrip == -1) {
            viewModel.selectTrip(viewModel.tripItemViewModelList.size - 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                startLocationUpdate()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        WorkManager.getInstance(this).cancelAllWorkByTag(Repository.WEATHER_JOB)
        unregisterReceiver(gpsReceiver)
    }

    private fun initViews() {
        setupRecyclerView()

        button_add_trip.setOnClickListener {
            val intent = Intent(this@HomeActivity, NewTripActivity::class.java)
            startActivity(intent)
        }

        viewModel.tripEntity.observe(this, androidx.lifecycle.Observer { tripDataList ->
            (item_list.adapter as TripRecyclerViewAdapter).updateList(
                viewModel.getTripItemVMList(
                    tripDataList
                ).reversed()
            )
            if (twoPane) {
                if (tripDataList.isEmpty()) {
                    supportFragmentManager.findFragmentById(R.id.trip_detail_container)?.let {
                        supportFragmentManager.beginTransaction().remove(it).commit()
                    }
                }
                viewModel.selectTrip(viewModel.tripItemViewModelList.size - 1)
                selectFirstItem(Handler(), item_list)
            }
        })

    }

    private fun setupRecyclerView() {
        item_list.adapter = TripRecyclerViewAdapter(
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
            viewModel.selectTrip(-1)
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

    private fun startLocationUpdate() {
        viewModel.locationData.observe(this, Observer {
            viewModel.saveLocationCoordinates(it.latitude, it.longitude)
            viewModel.getCurrentWeather(it.latitude, it.longitude)
        })
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (isGPSEnabled) {
                        startLocationUpdate()
                    } else {
                        GpsUtil(this).turnGPSOn(object : GpsUtil.OnGpsListener {
                            override fun gpsStatus(isGPSEnabled: Boolean) {
                                this@HomeActivity.isGPSEnabled = isGPSEnabled
                                if (isGPSEnabled) startLocationUpdate()
                            }
                        })
                    }
                }
            }
        }
    }

    companion object {
        const val GPS_REQUEST = 101
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 1989
    }
}
