package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.navigateBackResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.extensions.AnyResultMutableLiveData
import com.rasalexman.sresultpresentation.extensions.clear

open class BaseViewModel : BaseContextViewModel(), IBaseViewModel {

    override val eventLiveData by unsafeLazy { MutableLiveData<com.rasalexman.sresult.data.dto.ISEvent>() }
    override val navigationLiveData by unsafeLazy { MutableLiveData<com.rasalexman.sresult.data.dto.SResult.NavigateResult>() }
    override val anyLiveData: LiveData<*>? = null
    override val resultLiveData: LiveData<*>? = null
    override val supportLiveData by unsafeLazy { AnyResultMutableLiveData() }
    // all data sequenced with onEvent extension
    override val liveDataToObserve: MutableList<LiveData<*>> = mutableListOf()

    // selected page of view pager
    override val selectedPage: MutableLiveData<Int> = MutableLiveData()
    // toolbar title live data
    override val toolbarTitle: MutableLiveData<String>? = null
    // toolbar subtitle live data
    override val toolbarSubTitle: MutableLiveData<String>? = null
    // toolbar menu res id
    override val toolbarMenu: MutableLiveData<Int>? = null

    /**
     * Handle Some error states with [SResult.AbstractFailure]
     */
    override fun handleErrorState(errorResult: com.rasalexman.sresult.data.dto.SResult.AbstractFailure) {
        supportLiveData.value = createFailure(errorResult)
    }

    override fun processEvent(viewEvent: com.rasalexman.sresult.data.dto.ISEvent) {
        eventLiveData.value = viewEvent
    }

    override fun processEventAsync(viewEvent: com.rasalexman.sresult.data.dto.ISEvent) {
        eventLiveData.postValue(viewEvent)
    }

    /**
     * When need to go back from layout
     * Don't forget to set canGoBack in Fragment to true
     */
    override fun onBackClicked() {
        navigationLiveData.value = navigateBackResult()
    }

    override fun onCleared() {
        clearData()
        super.onCleared()
    }

    protected open fun clearData() {
        resultLiveData.clear()
        anyLiveData.clear()
        supportLiveData.clear()
        navigationLiveData.clear()
        eventLiveData.clear()
    }
}
