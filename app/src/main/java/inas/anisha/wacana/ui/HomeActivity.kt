package inas.anisha.wacana.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import inas.anisha.wacana.R
import inas.anisha.wacana.dataModel.TripDataModel
import inas.anisha.wacana.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [TripDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class HomeActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        if (trip_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(item_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = TripRecyclerViewAdapter(
            DummyContent.ITEMS,
            object : TripRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(tripDetail: TripDataModel) {

                    if (twoPane) {
                        val fragment = TripDetailFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(TripDetailFragment.ARG_ITEM_ID, tripDetail)
                            }
                        }
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.trip_detail_container, fragment)
                            .commit()
                    } else {
//                        val intent = Intent(this@HomeActivity, TripActivity::class.java)
//                        startActivity(intent)
                        val intent =
                            Intent(this@HomeActivity, TripDetailActivity::class.java).apply {
                                putExtra(TripDetailFragment.ARG_ITEM_ID, tripDetail)
                            }
                        startActivity(intent)
                    }
                }
            }
        )
    }

}
