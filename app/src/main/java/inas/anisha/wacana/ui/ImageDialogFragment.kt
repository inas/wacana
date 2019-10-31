package inas.anisha.wacana.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ImageLayoutBinding
import java.io.File

class ImageDialogFragment : DialogFragment() {

    lateinit var binding: ImageLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.image_layout, container, false)
        binding.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageDialog = dialog
        if (imageDialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            imageDialog.window?.setLayout(width, height)
        }

        requireContext().let {
            val uri = Uri.parse(arguments?.getString(IMAGE_DIALOG))
            Glide.with(it).load(File(uri.path)).into(binding.imageView)
        }

    }

    companion object {
        val IMAGE_DIALOG = "IMAGE_DIALOG"
    }
}