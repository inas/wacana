package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.baggageTab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.databinding.BaggageItemBinding


class BaggageItemRecyclerViewAdapter(
    private var lifecycleOwner: LifecycleOwner,
    private var data: MutableList<BaggageItemViewModel>,
    private var clickListener: OnDeleteClickListener
) :
    RecyclerView.Adapter<BaggageItemRecyclerViewAdapter.ViewHolder>() {

    lateinit var binding: BaggageItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.baggage_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = holder.binding
        data[position].let {
            item.viewModel = it
            item.baggageItemCheckbox.setOnCheckedChangeListener { _, isChecked ->
                it.checkedState.value = isChecked
            }
            it.enabledState.observe(lifecycleOwner, Observer { enabled ->
                if (enabled) {
                    item.baggageItemButtonRemove.visibility = View.VISIBLE
                    item.baggageItemCheckbox.isEnabled = true
                } else {
                    item.baggageItemButtonRemove.visibility = View.INVISIBLE
                    item.baggageItemCheckbox.isEnabled = false
                }
            })
        }

        item.baggageItemButtonRemove.setOnClickListener {
            clickListener.onDeleteClick(holder.adapterPosition)
        }
    }

    inner class ViewHolder(val binding: BaggageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    fun updateList(newData: MutableList<BaggageItemViewModel>) {
        data = newData
        notifyDataSetChanged()
    }

    fun addItem() {
        notifyItemInserted(data.size + 1)
    }

    fun removeItem(index: Int) {
        notifyItemRemoved(index)
    }

}