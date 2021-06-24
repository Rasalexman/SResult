package com.rasalexman.sresultexample.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.logg
import com.rasalexman.sresult.models.IDropDownItem
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.extensions.mutableSwitchMap
import com.rasalexman.sresultpresentation.viewModels.SharedSavedStateViewModel

class ProfileLayoutViewModel : SharedSavedStateViewModel() {

    private val item: LiveData<String> = savedStateHandler.getLiveData("itemId")
    private val dropDownItems = mutableListOf<IDropDownItem>()

    val generatedItems: MutableLiveData<List<IDropDownItem>> = item.mutableSwitchMap {
        asyncLiveData {
            logg { "------> ProfileLayoutViewModel item = $it" }
            dropDownItems.add(object : IDropDownItem {
                override val itemId: String = it
                override val title: String = it
            })
            emit(dropDownItems)
        }
    }

    val selectedItem: MutableLiveData<IDropDownItem> = MutableLiveData()

}