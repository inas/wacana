package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.baggageTab

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.Repository
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.ItemEntity

class BaggageTabViewModel(application: Application) : AndroidViewModel(application) {
    var isInEditMode: MutableLiveData<Boolean> = MutableLiveData(false)
    var itemList: LiveData<List<ItemEntity>> = MutableLiveData(mutableListOf())
    var itemViewModels: MutableList<BaggageItemViewModel> = mutableListOf()
    var tripId: Long = 0

    fun initViewModel(id: Long?) {
        id?.let {
            tripId = it
            itemList = Repository.getInstance(getApplication()).getAllItems(it)
        }

        itemList.value?.let {
            itemViewModels = initItemViewModels(it)
        }
    }

    fun initItemViewModels(entities: List<ItemEntity>): MutableList<BaggageItemViewModel> {
        itemViewModels = entities.map {
            BaggageItemViewModel().apply {
                itemId = it.id
                itemName = it.itemName
                checkedState = MutableLiveData(it.isSelected)
            }
        } as MutableList
        return itemViewModels
    }

    fun deleteItem(position: Int) {
        itemViewModels.removeAt(position)
    }

    fun addItem(name: String): BaggageItemViewModel {
        val item = BaggageItemViewModel().apply {
            itemName = name
            checkedState.value = false
            enabledState.value = true
        }
        itemViewModels.add(item)
        return item
    }

    fun saveItems() {
        val entities = itemViewModels.map {
            ItemEntity(0, it.itemName, it.isChecked(), tripId)
        } as MutableList
        Repository.getInstance(getApplication()).insertItems(entities, tripId)
    }

    fun changeToEditMode(isEditMode: Boolean) {
        isInEditMode.value = isEditMode
        itemViewModels.forEach { it.enabledState.value = isEditMode }
    }

    fun abortChanges() {
        itemViewModels = initItemViewModels(itemList.value ?: mutableListOf())
    }
}