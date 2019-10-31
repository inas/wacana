package inas.anisha.wacana.ui.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import inas.anisha.wacana.ui.DocumentTabFragment
import inas.anisha.wacana.ui.DocumentTabViewModel
import java.util.*

private val TAB_TITLES = arrayOf(
    "tab 1",
    "tab 2"
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    private val documentTabViewModel: DocumentTabViewModel
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0) return DocumentTabFragment().apply {
            arguments = Bundle().apply {
                putStringArrayList(
                    DocumentTabFragment.DOCUMENTS,
                    documentTabViewModel.uriList as ArrayList<String>
                )
                putLong(DocumentTabFragment.TRIP_ID, documentTabViewModel.tripId)
            }
        }
        return PlaceholderFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        return 2
    }
}