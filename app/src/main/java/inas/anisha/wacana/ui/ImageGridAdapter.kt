package inas.anisha.wacana.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ImageLayoutBinding


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
//        val contentUri = Uri.parse(data[position])
//        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
//        cursor?.let {
//
//        }
//        Glide.with(context).load(uri).into(binding.imageView)
        val data = data[position]
        binding.imageView.setImageURI(Uri.parse(data))
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