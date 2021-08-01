package com.rasalexman.sresultpresentation.viewModels.flowable

import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.viewModels.IEventableViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

interface IFlowableViewModel : IEventableViewModel {
    val selectedPage: MutableStateFlow<Int>

    val eventsFlow: MutableSharedFlow<ISEvent>
    val navigationFlow: MutableSharedFlow<SResult.NavigateResult>
    val anyDataFlow: Flow<*>?
    val resultFlow: Flow<*>?
    val supportFlow: MutableSharedFlow<SResult<Any>>

    val toolbarTitle: MutableStateFlow<String>?
    val toolbarSubTitle: MutableStateFlow<String>?

    fun clear()
}