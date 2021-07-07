package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.MutableLiveData

abstract class BaseViewPagerViewModel : BaseViewModel() {

    open val items: MutableLiveData<List<BaseViewModel>> = MutableLiveData()
}