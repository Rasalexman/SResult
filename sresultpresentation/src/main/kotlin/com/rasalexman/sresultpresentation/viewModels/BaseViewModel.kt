package com.rasalexman.sresultpresentation.viewModels

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.immutableInstance
import com.rasalexman.sresult.common.extensions.errorResult
import com.rasalexman.sresult.common.extensions.navigateBackResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResultMutableLiveData
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

open class BaseViewModel : ViewModel(), IKodi, IBaseViewModel {

    protected val context: Context by immutableInstance()
    val superVisorJob: Job by lazy { SupervisorJob() }

    override val eventLiveData by unsafeLazy { MutableLiveData<ISEvent>() }
    override val navigationLiveData by unsafeLazy { MutableLiveData<SResult.NavigateResult>() }
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

    /**
     * Handle Some error states with [SResult.AbstractFailure]
     */
    open fun handleErrorState(errorResult: SResult.AbstractFailure) {
        supportLiveData.value = errorResult(message = errorResult.message.toString(), exception = errorResult.exception)
    }

    /**
     * Process [SEvent] from Presentation View Controller (such as Activity or Fragment) to ViewModel
     *
     * @param viewEvent [SEvent] - any implementation to handler with this fragment
     */
    override fun processViewEvent(viewEvent: ISEvent) {
        eventLiveData.value = viewEvent
    }

    /**
     * Process [SEvent] from Presentation View Controller (such as Activity or Fragment) to ViewModel
     *
     * @param viewEvent [SEvent] - any implementation to handler with this fragment
     */
    override fun processEventAsync(viewEvent: ISEvent) {
        eventLiveData.postValue(viewEvent)
    }

    /**
     * When need to go back from layout
     * Don't forget to set canGoBack in Fragment to true
     */
    fun onBackClicked() {
        navigationLiveData.value = navigateBackResult()
    }

    protected fun BaseViewModel.string(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}
