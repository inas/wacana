package inas.anisha.wacana.ui.home

import android.Manifest
import android.annotation.SuppressLint
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
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import inas.anisha.wacana.R
import inas.anisha.wacana.Repository
import inas.anisha.wacana.databinding.ActivityHomeBinding
import inas.anisha.wacana.ui.newTrip.NewTripActivity
import inas.anisha.wacana.ui.tripDetail.TripDetailActivity
import inas.anisha.wacana.ui.tripDetail.TripDetailTabLayoutFragment
import inas.anisha.wacana.ui.tripList.TripRecyclerViewAdapter
import inas.anisha.wacana.util.GpsUtil
import kotlinx.android.synthetic.main.trip_list.*


class HomeActivity : AppCompatActivity() {

    lateinit var viewModel: HomeViewModel
    lateinit var binding: ActivityHomeBinding
    lateinit var gpsReceiver: BroadcastReceiver

    private var twoPane: Boolean = false
    private var isGPSEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home, null)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = title

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.initViewModel()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        GpsUtil(this).turnGPSOn(object : GpsUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnabled: Boolean) {
                this@HomeActivity.isGPSEnabled = isGPSEnabled
            }
        })
    }

    override fun onStart() {
        super.onStart()
        twoPane = trip_detail_container != null
        initViews()
        invokeLocationAction()

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
                            GpsUtil(context).turnGPSOn(object : GpsUtil.OnGpsListener {
                                override fun gpsStatus(isGPSEnabled: Boolean) {
                                    this@HomeActivity.isGPSEnabled = isGPSEnabled
                                }
                            })
                            invokeLocationAction()
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
                isGPSEnabled = true
                invokeLocationAction()
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
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
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

    private fun invokeLocationAction() {
        when {
            !isGPSEnabled -> Log.d("debugweather", "gps ga enabled")

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> Log.d("debugweather", "request dong :(")

            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_ACCESS_LOCATION
            )
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
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_LOCATION -> {
                invokeLocationAction()
            }
        }
    }

    companion object {
        const val GPS_REQUEST = 101
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 1989
    }
}
