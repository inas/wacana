package inas.anisha.wacana.ui.documentTab

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.FragmentTabDocumentBinding
import inas.anisha.wacana.ui.home.HomeActivity
import inas.anisha.wacana.ui.tripDetail.TripDetailActivity
import inas.anisha.wacana.util.FileUtil

class BaggageTabFragment : Fragment() {

    private lateinit var viewModel: BaggageTabViewModel
    private lateinit var binding: FragmentTabDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BaggageTabViewModel::class.java).apply {
            initViewModel(arguments?.getLong(TRIP_ID))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tab_document, container, false)
        requireContext().let {
            binding.tabDocumentRecyclerView.layoutManager =
                GridAutofitLayoutManager(
                    it, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 128f,
                        it.resources.displayMetrics
                    ).toInt()
                )

        }

        binding.tabDocumentFab.setOnClickListener { browseImage() }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_GALLERY_REQUEST) {
            requireContext().let { context ->
                data?.data?.let { viewModel.addDocument(FileUtil.getPath(context, it)) }
            }
        }
    }

    fun browseImage() {
        requireContext().let { context ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                (if (activity is HomeActivity) activity as HomeActivity else activity as TripDetailActivity).let {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            } else {
                openGallery()
            }
        }
    }

    fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            IMAGE_GALLERY_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery()
                }
                return
            }
        }
    }

    companion object {
        const val TRIP_ID = "TRIP_ID"
        const val IMAGE_GALLERY_REQUEST = 1
        const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2
    }

}