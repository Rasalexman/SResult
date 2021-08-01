package com.rasalexman.sresultpresentation.viewModels

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.immutableInstance
import com.rasalexman.sresult.common.extensions.doOnDefault
import com.rasalexman.sresult.common.extensions.errorResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

abstract class BaseContextViewModel : ViewModel(), IKodi, IResultViewModel {
    protected val context: Context by immutableInstance()
    val superVisorJob: Job by lazy { SupervisorJob() }

    val statesScope: CoroutineScope by unsafeLazy {
        CoroutineScope(this.viewModelScope.coroutineContext + superVisorJob)
    }

    /**
     * Process [com.rasalexman.sresult.data.dto.SEvent] from Presentation View Controller (such as Activity or Fragment) to ViewModel
     *
     * @param viewEvent [SEvent] - any implementation to handler with this fragment
     */
    override fun processViewEvent(viewEvent: ISEvent) = Unit

    /**
     * Process [SEvent] from Presentation View Controller (such as Activity or Fragment) to ViewModel
     *
     * @param viewEvent [SEvent] - any implementation to handler with this fragment
     */
    override fun processEventAsync(viewEvent: ISEvent) = Unit

    abstract fun onBackClicked()

    protected open fun createFailure(errorResult: SResult.AbstractFailure): SResult.AbstractFailure {
        return errorResult(message = errorResult.message.toString(), exception = errorResult.exception)
    }

    protected fun BaseContextViewModel.string(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    protected suspend fun BaseContextViewModel.stringSuspend(@StringRes resId: Int): String {
        return doOnDefault { context.getString(resId) }
    }
}