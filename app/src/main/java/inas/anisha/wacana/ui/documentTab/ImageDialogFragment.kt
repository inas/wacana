package inas.anisha.wacana.ui.documentTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.FragmentDialogImageBinding

class ImageDialogFragment : DialogFragment() {

    lateinit var binding: FragmentDialogImageBinding
    lateinit var viewModel: ImageDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        viewModel = ViewModelProviders.of(this).get(ImageDialogViewModel::class.java).apply {
            initViewModel(
                arguments?.getLong(IMAGE_ID) ?: 0, arguments?.getString(
                    FILE_PATH
                ) ?: ""
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_image, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        binding.fragmentDialogImageViewClose.setOnClickListener { dismiss() }
        binding.fragmentDialogImageViewDelete.setOnClickListener {
            viewModel.removeImage()
            dismiss()
        }

        val imageDialog = dialog
        if (imageDialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            imageDialog.window?.setLayout(width, height)
        }

        dialog?.setCanceledOnTouchOutside(true)

        requireContext().let {
            Glide.with(it)
                .load(viewModel.imageFilePath)
                .into(binding.imageView)
        }

    }

    companion object {
        val FILE_PATH = "FILE_PATH"
        val IMAGE_ID = "IMAGE_ID"
    }
}