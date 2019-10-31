package inas.anisha.wacana.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.FragmentTabDocumentBinding


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

        viewModel.documentList.observe(this, Observer {
            val data = viewModel.getUris(it)
            (binding.tabDocumentRecyclerView.adapter as ImageGridAdapter).updateList(data)
//            requireContext().let { context ->
//                binding.tabDocumentRecyclerView.adapter = ImageGridAdapter(context, data,
//                    object : ImageGridAdapter.OnItemClickListener {
//                        override fun onItemClick(position: Int) {
//                            fragmentManager?.beginTransaction()?.let { ft ->
//                                ImageDialogFragment().apply {
//                                    arguments = Bundle().apply {
//                                        putString(
//                                            ImageDialogFragment.IMAGE_DIALOG,
//                                            viewModel.uriList[position]
//                                        )
//                                    }
//                                }.show(ft, "")
//                            }
//                        }
//                    })
//            }
        })

        binding.tabDocumentFab.setOnClickListener { browseImage() }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_GALLERY_REQUEST) {
////            data?.data?.let { viewModel.addDocument(it) }
//            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//            // Get the cursor
//            requireContext().let { context ->
//                data?.data?.let {
//                    val cursor = context.contentResolver.query(it, filePathColumn, null, null, null)
//                    cursor?.apply {
//                        moveToFirst()
//                        val columnIndex = getColumnIndex(filePathColumn[0])
//                        viewModel.addDocument(getString(columnIndex))
//                    }?.close()
//                }
//            }

//            data?.data?.let {
//                val path = getPathFromURI(it)
//                if (path != null) {
//                    val f = File(path)
//                    viewModel.addDocument(Uri.fromFile(f).toString())
//                }
//            }
            // Get the path from the Uri

            data?.data?.let { viewModel.addDocument(FilePath.getPath(activity, it)) }
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
//                val intent = Intent()
//                intent.type = "image/*"
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(
//                    Intent.createChooser(intent, "Select Picture"),
//                    IMAGE_GALLERY_REQUEST
//                )
                openGallery()
            }
        }


    }

    fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        // Sets the type as image/*. This ensures only components of type image are selected
//        intent.type = "image/*"
//        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
//        val mimeTypes = arrayOf("image/jpeg", "image/png")
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//        startActivityForResult(intent, IMAGE_GALLERY_REQUEST)
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
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    /* Check whether this uri is a content uri or not.
*  content uri like content://media/external/images/media/1302716
*  */
    fun isContentUri(uri: Uri) = "content".equals(uri.scheme, true)

    /* Check whether this uri is a file uri or not.
    *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
    * */
    fun isFileUri(uri: Uri) = "file".equals(uri.scheme, true)


    /* Check whether this document is provided by ExternalStorageProvider. */
    fun isExternalStoreDoc(uriAuthority: String) =
        "com.android.externalstorage.documents" == uriAuthority

    /* Check whether this document is provided by DownloadsProvider. */
    fun isDownloadDoc(uriAuthority: String) =
        "com.android.providers.downloads.documents" == uriAuthority

    /* Check whether this document is provided by MediaProvider. */
    fun isMediaDoc(uriAuthority: String) = "com.android.providers.media.documents" == uriAuthority

    /* Check whether this document is provided by google photos. */
    fun isGooglePhotoDoc(uriAuthority: String) =
        "com.google.android.apps.photos.content" == uriAuthority

    /* Return uri represented document file real local path.*/
    fun getImageRealPath(contentResolver: ContentResolver, uri: Uri, whereClause: String?): String {
        var ret = ""

        // Query the uri with condition.
        val cursor = contentResolver.query(uri, null, whereClause, null, null)
        cursor?.also {
            val moveToFirst = it.moveToFirst()
            if (moveToFirst) {
                // Get columns name by uri type.
                var columnName = MediaStore.Images.Media.DATA
                when (uri) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> columnName =
                        MediaStore.Images.Media.DATA
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI -> columnName =
                        MediaStore.Audio.Media.DATA
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> columnName =
                        MediaStore.Video.Media.DATA
                }

                // Get column index.
                val imageColumnIndex = it.getColumnIndex(columnName)

                // Get column value which is the uri related file local path.
                ret = it.getString(imageColumnIndex)
            }
        }?.close()

        return ret
    }

    companion object {
        const val TRIP_ID = "TRIP_ID"
        const val IMAGE_GALLERY_REQUEST = 1
        const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2
    }

    fun getPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        requireContext().let {
            it.contentResolver.query(contentUri, proj, null, null, null).apply {
                if (moveToFirst()) {
                    val column_index = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    res = this.getString(column_index)
                }
            }?.close()
        }
        return res
    }
}