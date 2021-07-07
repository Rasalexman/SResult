package com.rasalexman.sresultexample.bottom.pages

import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

class StartBottomViewModel : BaseViewModel() {

    fun onNextClicked() {
        navigationLiveData.value = StartBottomFragmentDirections.showNextBottomFragment().toNavigateResult()
    }
}