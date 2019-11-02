package inas.anisha.wacana.ui.tripDetail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ActivityTripDetailBinding
import inas.anisha.wacana.db.entity.TripEntity
import inas.anisha.wacana.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_trip_detail.*

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [HomeActivity].
 */
class TripDetailActivity : AppCompatActivity() {

    lateinit var viewModel: TripDetailViewModel
    lateinit var binding: ActivityTripDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_detail, null)
        setSupportActionBar(detail_toolbar)
        viewModel = ViewModelProviders.of(this).get(TripDetailViewModel::class.java)
        binding.viewModel = viewModel
        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
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
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                navigateUpTo(Intent(this, HomeActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
