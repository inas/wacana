package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.documentTab

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.databinding.FragmentTabDocumentBinding
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.home.HomeActivity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.tripDetail.TripDetailActivity
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.util.FileUtil

class DocumentTabFragment : Fragment() {

    private lateinit var viewModel: DocumentTabViewModel
    private lateinit var binding: FragmentTabDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DocumentTabViewModel::class.java).apply {
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
            binding.tabDocumentRecyclerView.adapter =
                ImageGridAdapter(it, viewModel.uriList,
                    object :
                        ImageGridAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            fragmentManager?.beginTransaction()?.let { ft ->
                                ImageDialogFragment().apply {
                                    arguments = Bundle().apply {
                                        putString(
                                            ImageDialogFragment.FILE_PATH,
                                            this@DocumentTabFragment.viewModel.uriList[position]
                                        )
                                        putLong(
                                            ImageDialogFragment.IMAGE_ID,
                                            this@DocumentTabFragment.viewModel.imageIdList[position]
                                        )
                                    }
                                }.show(ft, "")
                            }
                        }
                    })

        }

        viewModel.documentList.observe(this, Observer {
            val data = viewModel.getUris(it)
            (binding.tabDocumentRecyclerView.adapter as ImageGridAdapter).updateList(data)
            binding.tabDocumentNoItems.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
        })

        binding.tabDocumentButton.setOnClickListener { browseImage() }
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

    private fun browseImage() {
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

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            IMAGE_GALLERY_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openGallery()
            }
        }
    }

    companion object {
        const val TRIP_ID = "TRIP_ID"
        const val IMAGE_GALLERY_REQUEST = 1
        const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2
    }

}