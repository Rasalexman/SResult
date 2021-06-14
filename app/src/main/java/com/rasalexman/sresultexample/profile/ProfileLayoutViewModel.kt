package com.rasalexman.sresultexample.profile

import androidx.lifecycle.LiveData
import com.rasalexman.sresult.common.extensions.logg
import com.rasalexman.sresultpresentation.extensions.mutableMap
import com.rasalexman.sresultpresentation.viewModels.SharedSavedStateViewModel

class ProfileLayoutViewModel : SharedSavedStateViewModel() {

    private val item: LiveData<String> = savedStateHandler.getLiveData("itemId")

    val itemId = item.mutableMap {
        logg { "------> ProfileLayoutViewModel item = ${item.hashCode()} | $it" }
        it
    }

}