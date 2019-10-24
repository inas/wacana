package inas.anisha.wacana.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import inas.anisha.wacana.R
import inas.anisha.wacana.dataModel.TripDataModel
import inas.anisha.wacana.databinding.TripListItemBinding

class TripRecyclerViewAdapter(
    private val data: List<TripItemViewModel>,
    private val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<TripRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<TripListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.trip_list_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel = data[position]
        holder.itemView.setOnClickListener { clickListener.onItemClick(data[position].tripDetail) }
    }

    inner class ViewHolder(val binding: TripListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClickListener {
        fun onItemClick(tripDetail: TripDataModel)
    }

}