package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.tripDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.databinding.ActivityTripDetailBinding
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.TripEntity
import kotlinx.android.synthetic.main.activity_trip_detail.*

class TripDetailActivity : AppCompatActivity() {

    lateinit var viewModel: TripDetailViewModel
    lateinit var binding: ActivityTripDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_detail, null)
        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this).get(TripDetailViewModel::class.java)
        binding.viewModel = viewModel

        if (savedInstanceState == null) {
            val fragment = TripDetailTabLayoutFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(
                        TripDetailTabLayoutFragment.ARG_TRIP_ID,
                        intent.getParcelableExtra<TripEntity>(TripDetailTabLayoutFragment.ARG_TRIP_ID)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.trip_detail_container, fragment)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.initViewModel(intent.getParcelableExtra(TripDetailTabLayoutFragment.ARG_TRIP_ID))

        binding.tripDetailButtonDelete.setOnClickListener {
            viewModel.deleteTrip()
            finish()
        }
    }

}
