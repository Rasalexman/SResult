package com.rasalexman.sresultexample.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.rasalexman.sresult.common.extensions.logg
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.extensions.mutableMap
import com.rasalexman.sresultpresentation.extensions.mutableSwitchMap
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import com.rasalexman.sresultpresentation.viewModels.SharedSavedStateViewModel
import java.util.*

class ProfileViewModel(
    private val savedStateHandle: SavedStateHandle
) : SharedSavedStateViewModel(savedStateHandle) {

    private val item: LiveData<String> = savedStateHandle.getLiveData("itemId")

    override val toolbarTitle: MutableLiveData<String> = item.mutableSwitchMap {
        logg { "------> ProfileViewModel item = ${item.hashCode()} | $it" }
        MutableLiveData(string(R.string.title_profile) + "_"+ it)
    }

    override val toolbarSubTitle: MutableLiveData<String> by unsafeLazy {
        MutableLiveData(string(R.string.title_profile))
    }

    fun onGenerateClicked() {
        savedStateHandle.set("itemId", UUID.randomUUID().toString().take(5))
    }
}