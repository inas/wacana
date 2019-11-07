package inas.anisha.wacana.ui.baggageTab

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BaggageItemViewModel : ViewModel() {

    var itemId: Long = 0
    var itemName: String = ""
    var checkedState: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var enabledState: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }

    fun isChecked() = checkedState.value ?: false
    fun isEnabled() = enabledState.value ?: false
    fun isVisible() = if (isEnabled()) View.VISIBLE else View.GONE
}