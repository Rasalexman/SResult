package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.MutableLiveData

abstract class BaseViewPagerViewModel : BaseViewModel() {

    open val items: MutableLiveData<List<BasePageViewModel>> = MutableLiveData()

    override fun onCleared() {
        val vmItems = items.value?.toList().orEmpty()
        vmItems.forEach {
            it.clearPage()
        }
        super.onCleared()
    }
}