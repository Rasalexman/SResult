package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult

interface IBaseViewModel {
    fun processViewEvent(viewEvent: ISEvent)

    val selectedPage: MutableLiveData<Int>
    val eventLiveData: MutableLiveData<ISEvent>
    val navigationLiveData: MutableLiveData<SResult.NavigateResult>
    val anyLiveData: LiveData<*>?
    val resultLiveData: LiveData<*>?
    val supportLiveData: MutableLiveData<SResult<Any>>

    val liveDataToObserve: MutableList<LiveData<*>>
}