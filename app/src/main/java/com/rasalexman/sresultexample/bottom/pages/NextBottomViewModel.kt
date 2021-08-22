package com.rasalexman.sresultexample.bottom.pages

import com.rasalexman.sresult.common.extensions.navigatePop
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

class NextBottomViewModel : BaseViewModel() {

    fun onCloseClicked() {
        navigationLiveData.value = navigatePop(mapOf("BACK" to "true"))
    }
}