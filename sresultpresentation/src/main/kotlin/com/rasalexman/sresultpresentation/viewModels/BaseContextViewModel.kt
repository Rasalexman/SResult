package com.rasalexman.sresultpresentation.viewModels

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.immutableInstance
import com.rasalexman.sresult.data.dto.ISEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

abstract class BaseContextViewModel : ViewModel(), IKodi, IResultViewModel {
    protected val context: Context by immutableInstance()
    val superVisorJob: Job by lazy { SupervisorJob() }

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

    protected fun BaseContextViewModel.string(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}