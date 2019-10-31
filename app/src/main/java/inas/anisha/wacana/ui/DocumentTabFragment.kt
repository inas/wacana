package inas.anisha.wacana.ui

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.FragmentTabDocumentBinding

class DocumentTabFragment : Fragment() {

    private lateinit var viewModel: DocumentTabViewModel
    private lateinit var binding: FragmentTabDocumentBinding
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DocumentTabViewModel::class.java).apply {
            initViewModel(arguments?.getStringArrayList(DOCUMENTS), arguments?.getLong(TRIP_ID))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tab_document, container, false)
        requireContext().let {
            binding.tabDocumentRecyclerView.layoutManager = GridLayoutManager(it, 3)
            binding.tabDocumentRecyclerView.adapter = ImageGridAdapter(it, viewModel.uriList,
                object : ImageGridAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        fragmentManager?.beginTransaction()?.let { ft ->
                            ImageDialogFragment().apply {
                                arguments = Bundle().apply {
                                    putString(
                                        ImageDialogFragment.IMAGE_DIALOG,
                                        viewModel.uriList[position]
                                    )
                                }
                            }.show(ft, "")
                        }
                    }
                })

        }

        binding.tabDocumentFab.setOnClickListener { browseImage() }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent.getStringArrayListExtra(DOCUMENTS).let {
                    viewModel.setUris(it)
                    (binding.tabDocumentRecyclerView.adapter as ImageGridAdapter).updateList(it)
                }
            }
        }
        val filter = IntentFilter(DOCUMENT_BROADCAST)
        activity?.registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        if (broadcastReceiver != null) {
            try {
                activity?.unregisterReceiver(broadcastReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_GALLERY_REQUEST) {
            data?.data?.let { viewModel.addDocument(it) }

        }

    }

    fun browseImage() {
        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT)
        gallery.type = "image/*"
        startActivityForResult(gallery, IMAGE_GALLERY_REQUEST)
    }

    companion object {
        const val DOCUMENT_BROADCAST = "DOCUMENT_BROADCAST"
        const val DOCUMENTS = "DOCUMENTS"
        const val TRIP_ID = "TRIP_ID"
        const val IMAGE_GALLERY_REQUEST = 1
    }
}