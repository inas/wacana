package inas.anisha.wacana.ui.baggageTab

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.FragmentTabBaggageBinding

class BaggageTabFragment : Fragment() {

    private lateinit var viewModel: BaggageTabViewModel
    private lateinit var binding: FragmentTabBaggageBinding
    private lateinit var baggageItemsRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BaggageTabViewModel::class.java).apply {
            initViewModel(arguments?.getLong(TRIP_ID))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tab_baggage, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initRecyclerView()

        binding.tabBaggageButtonEdit.setOnClickListener {
            viewModel.changeToEditMode(true)
            binding.tabBaggageTextViewNoItems.visibility = View.GONE
        }

        binding.tabBaggageButtonSave.setOnClickListener {
            viewModel.saveItems()
            viewModel.changeToEditMode(false)
            binding.tabBaggageTextViewNoItems.visibility =
                if (viewModel.itemViewModels.isEmpty()) View.VISIBLE else View.INVISIBLE
        }

        binding.tabBaggageEditText.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                val item = viewModel.addItem(binding.tabBaggageEditText.text.toString())
                (baggageItemsRV.adapter as BaggageItemRecyclerViewAdapter).addItem()
                binding.tabBaggageEditText.setText("")
                true
            } else {
                false
            }
        }

        binding.tabBaggageButtonCancel.setOnClickListener {
            viewModel.abortChanges()
            viewModel.changeToEditMode(false)
            (baggageItemsRV.adapter as BaggageItemRecyclerViewAdapter).updateList(viewModel.itemViewModels)
            binding.tabBaggageTextViewNoItems.visibility =
                if (viewModel.itemViewModels.isEmpty()) View.VISIBLE else View.INVISIBLE
            binding.tabBaggageEditText.setText("")
        }

        viewModel.itemList.observe(this, Observer {
            (baggageItemsRV.adapter as BaggageItemRecyclerViewAdapter).updateList(
                viewModel.initItemViewModels(
                    it
                )
            )
            binding.tabBaggageTextViewNoItems.visibility =
                if (viewModel.itemViewModels.isEmpty()) View.VISIBLE else View.INVISIBLE
        })

        return binding.root
    }

    private fun initRecyclerView() {
        binding.tabBaggageTextViewNoItems.visibility =
            if (viewModel.itemViewModels.isEmpty()) View.VISIBLE else View.INVISIBLE
        baggageItemsRV = binding.tabBaggageRecyclerView
        baggageItemsRV.layoutManager = LinearLayoutManager(context)
        initRecyclerView(viewModel.itemViewModels)
    }

    private fun initRecyclerView(itemViewModels: MutableList<BaggageItemViewModel>) {
        baggageItemsRV.adapter = BaggageItemRecyclerViewAdapter(
            this,
            itemViewModels,
            object : BaggageItemRecyclerViewAdapter.OnDeleteClickListener {
                override fun onDeleteClick(position: Int) {
                    viewModel.deleteItem(position)
                    (baggageItemsRV.adapter as BaggageItemRecyclerViewAdapter).removeItem(position)
                }
            })
    }

    companion object {
        const val TRIP_ID = "TRIP_ID"
    }

}