package inas.anisha.wacana.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ImageLayoutBinding

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

        dialog?.setCanceledOnTouchOutside(true)
        binding.imageView.setOnClickListener {
            dismiss()
        }

        requireContext().let {
            val uri = Uri.parse(arguments?.getString(IMAGE_DIALOG))
            binding.imageView.setImageURI(uri)
        }

    }

    companion object {
        val IMAGE_DIALOG = "IMAGE_DIALOG"
    }
}