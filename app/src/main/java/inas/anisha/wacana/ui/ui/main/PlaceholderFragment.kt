package inas.anisha.wacana.ui.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import inas.anisha.wacana.R

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trip_pager, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        pageViewModel.text.observe(this, Observer<String> {
            textView.text = it
        })
        val tett =
            "Layout Breakdown Some of the attributes here are what make the parallax scroll work. " +
                    "Let’s go through them, one by one. 21. The total height that we want our collapsible header " +
                    "View to have. 256dp is a good number. 25. This is not the Flexible Space with Image pattern. " +
                    "We don’t want the Toolbar title to collapse. We want it fixed at the top. So we disable the ‘collapsible’ title. " +
                    "34. Flag which triggers the parallax effect upon scrolling. 36–41 A Scrim that makes Tab text more readable " +
                    "against the busy header background. 47. Remember that CollapsingToolbarLayout is an extension of FrameLayout. " +
                    "This attribute ensures the Toolbar stays on top. 48. TabLayout by default has a height of 48dp. " +
                    "Telling our Toolbar to have a bottom margin of the same, avoids the overlap issue that we saw above. 49. " +
                    "Makes sure the Toolbar is consistently pinned to the top. Otherwise when you start scrolling, the Toolbar will scroll off-screen. " +
                    "56. Ensures the TabLayout sticks to the bottom of the header. A visual breakdown of the layout will give you a better idea."
        root.findViewById<TextView>(R.id.tab_text).text = tett + tett + tett + tett + tett
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}