package inas.anisha.wacana.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ImageLayoutBinding
import java.io.File


class ImageGridAdapter(
    private var context: Context,
    private var data: List<String>,
    private var clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ImageGridAdapter.ViewHolder>() {

    lateinit var binding: ImageLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate<ImageLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.image_layout,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener { clickListener.onItemClick(position) }
        Glide.with(context).load(File(Uri.parse(data[position]).path)).into(binding.imageView)
    }

    inner class ViewHolder(val binding: ImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun updateList(newData: List<String>) {
        data = newData
        notifyDataSetChanged()
    }

}