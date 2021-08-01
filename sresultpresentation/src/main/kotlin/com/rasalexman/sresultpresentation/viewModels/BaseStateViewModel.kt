package com.rasalexman.sresultpresentation.viewModels

import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

open class BaseStateViewModel : BaseContextViewModel(), IStateViewModel {

    protected open val supportReplay: Int = 1
    protected open val supportExtraBufferCapacity: Int = 1
    protected open val supportBufferOverflow: BufferOverflow = BufferOverflow.DROP_LATEST

    override val eventsFlow by unsafeLazy { MutableStateFlow<ISEvent>(SEvent.Empty) }
    override val navigationFlow by unsafeLazy { MutableStateFlow<SResult.NavigateResult>(SResult.NavigateResult.EmptyNavigation) }
    override val supportFlow: MutableSharedFlow<AnyResult> by unsafeLazy {
        MutableSharedFlow(
            replay = supportReplay,
            extraBufferCapacity = supportExtraBufferCapacity,
            onBufferOverflow = supportBufferOverflow
        )
    }

    // for any data flow
    override val anyDataFlow: MutableSharedFlow<*>? = null
    override val resultFlow: MutableSharedFlow<*>? = null


    // for pages
    override val selectedPage: MutableStateFlow<Int> by unsafeLazy { MutableStateFlow(0) }
    override val toolbarSubTitle: MutableStateFlow<String>? = null
    override val toolbarTitle: MutableStateFlow<String>? = null

    override fun processEventAsync(viewEvent: ISEvent) {
        eventsFlow.value = viewEvent
    }
}