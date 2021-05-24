package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.MutableLiveData

interface ICoreLoadingViewModel {
    val progress: MutableLiveData<Boolean>
    val elapsedTime: MutableLiveData<Long>
    fun showProgress()
    fun hideProgress()
    fun clean()
}