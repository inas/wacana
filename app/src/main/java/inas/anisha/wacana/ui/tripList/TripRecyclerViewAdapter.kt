package inas.anisha.wacana.ui.tripList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.TripListItemBinding


class TripRecyclerViewAdapter(
    private var context: Context,
    private var lifecycleOwner: LifecycleOwner,
    private var data: List<TripItemViewModel>,
    private var clickListener: OnItemClickListener
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
        holder.itemView.setOnClickListener { clickListener.onItemClick(position) }
        data[position].isSelected.observe(lifecycleOwner, Observer {
            val color = context.resources.getColor(if (it) R.color.yellow else R.color.white)
            holder.binding.tripListItemCard.setCardBackgroundColor(color)
        })
    }

    inner class ViewHolder(val binding: TripListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun updateList(newData: List<TripItemViewModel>) {
        data = newData
        notifyDataSetChanged()
    }

}