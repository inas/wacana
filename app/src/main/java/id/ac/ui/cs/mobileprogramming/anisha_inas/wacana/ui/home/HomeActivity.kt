package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.home

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkManager
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.Repository
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.databinding.ActivityHomeBinding
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.service.MusicService
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.service.MusicService.Companion.ACTION_PAUSE
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.service.MusicService.Companion.ACTION_PLAY
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

        if (!isPermissionGranted()) {
            requestPermission()
        } else {
            turnGPSOn()
        }
    }

    override fun onStart() {
        super.onStart()
        if (isPermissionGranted() && isGPSEnabled && !isNetworkAvailable()) {
            val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (!wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }
        }

        initViews()

        viewModel.weather.observe(this, Observer {
            viewModel.updateWeather(it)
            if (it.main?.temp == null || it.name == null || it.weather[0].main == null) {
                binding.tripListLayoutWeather.visibility = View.GONE
            } else {
                binding.tripListLayoutWeather.visibility = View.VISIBLE
                binding.tripListTextViewLocation.text = viewModel.locationName
                binding.tripListTextViewTemperature.text = viewModel.temperature
                binding.tripListTextViewWeather.text = viewModel.weatherDescription
            }
        })

        gpsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent.action?.let {
                    if (it.matches(LocationManager.PROVIDERS_CHANGED_ACTION.toRegex())) {
                        val locationManager =
                            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        isGPSEnabled =
                            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    }
                }
            }
        }

        registerReceiver(gpsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

        binding.mainButtonPlay.setOnClickListener {
            startService(Intent(this, MusicService::class.java).apply { action = ACTION_PLAY })
            it.visibility = View.INVISIBLE
            binding.mainButtonPause.visibility = View.VISIBLE
        }

        binding.mainButtonPause.setOnClickListener {
            startService(Intent(this, MusicService::class.java).apply { action = ACTION_PAUSE })
            it.visibility = View.INVISIBLE
            binding.mainButtonPlay.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GPS_REQUEST) turnGPSOn()
    }

    override fun onStop() {
        super.onStop()
        WorkManager.getInstance(this).cancelAllWorkByTag(Repository.WEATHER_JOB)
        unregisterReceiver(gpsReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MusicService::class.java))
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun initViews() {
        setupRecyclerView()

        binding.buttonAddTrip.setOnClickListener {
            val intent = Intent(this@HomeActivity, NewTripActivity::class.java)
            startActivity(intent)
        }

        viewModel.tripEntity.observe(this, androidx.lifecycle.Observer { tripDataList ->
            (item_list.adapter as TripRecyclerViewAdapter).updateList(
                viewModel.getTripItemVMList(
                    tripDataList
                ).reversed()
            )
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

    private fun openTripDetail(tripIndex: Int) {
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

    private fun isPermissionGranted() =
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

    private fun turnGPSOn() {
        GpsUtil(this).turnGPSOn(object : GpsUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnabled: Boolean) {
                this@HomeActivity.isGPSEnabled = isGPSEnabled
                if (isGPSEnabled) {
                    viewModel.initLocationData()
                    startLocationUpdate()
                }
            }
        })
    }

    private fun startLocationUpdate() {
        viewModel.locationData.observe(this, Observer {
            viewModel.saveLocationCoordinates(it.latitude, it.longitude)
            viewModel.getCurrentWeather(it.latitude, it.longitude)
        })
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
                    turnGPSOn()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        val alertBuilder = AlertDialog.Builder(this)
                        alertBuilder.setCancelable(true)
                        alertBuilder.setTitle("Are you sure?")
                        alertBuilder.setMessage("If you deny this permission app won't be able to show local weather")
                        alertBuilder.setPositiveButton("Allow") { _, _ ->
                            requestPermission()
                        }
                        alertBuilder.setNegativeButton("Deny") { _, _ -> }
                        val alert = alertBuilder.create()
                        alert.show()
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
