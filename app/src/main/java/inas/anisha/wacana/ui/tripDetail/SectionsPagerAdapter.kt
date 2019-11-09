package inas.anisha.wacana.ui.tripDetail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import inas.anisha.wacana.R
import inas.anisha.wacana.ui.baggageTab.BaggageTabFragment
import inas.anisha.wacana.ui.documentTab.DocumentTabFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    private val tripId: Long
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return if (position == 0) DocumentTabFragment().apply {
            arguments = Bundle().apply {
                putLong(DocumentTabFragment.TRIP_ID, tripId)
            }
        }
        else BaggageTabFragment().apply {
            arguments = Bundle().apply {
                putLong(BaggageTabFragment.TRIP_ID, tripId)
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(if (position == 0) R.string.tab_document_title else R.string.tab_baggage_title)
    }

    override fun getCount(): Int {
        return 2
    }

}