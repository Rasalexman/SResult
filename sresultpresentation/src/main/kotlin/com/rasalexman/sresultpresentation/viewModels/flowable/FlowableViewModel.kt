package com.rasalexman.sresultpresentation.viewModels.flowable

import com.rasalexman.sresult.common.extensions.navigateBackResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.viewModels.BaseContextViewModel
import com.rasalexman.sresultpresentation.viewModels.flowable.config.ISharedFlowConfig
import com.rasalexman.sresultpresentation.viewModels.flowable.config.SharedFlowConfig
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

open class FlowableViewModel : BaseContextViewModel(), IFlowableViewModel {

    protected open val eventConfig: ISharedFlowConfig = SharedFlowConfig()
    protected open val supportConfig: ISharedFlowConfig = SharedFlowConfig()
    protected open val navigationConfig: ISharedFlowConfig = SharedFlowConfig()

    override val eventsFlow by unsafeLazy {
        MutableSharedFlow<com.rasalexman.sresult.data.dto.ISEvent>(
            replay = eventConfig.replay,
            extraBufferCapacity = eventConfig.extraBufferCapacity,
            onBufferOverflow = eventConfig.onBufferOverflow
        )
    }
    override val navigationFlow by unsafeLazy {
        MutableSharedFlow<com.rasalexman.sresult.data.dto.SResult.NavigateResult>(
            replay = navigationConfig.replay,
            extraBufferCapacity = navigationConfig.extraBufferCapacity,
            onBufferOverflow = navigationConfig.onBufferOverflow
        )
    }
    override val supportFlow: MutableSharedFlow<AnyResult> by unsafeLazy {
        MutableSharedFlow(
            replay = supportConfig.replay,
            extraBufferCapacity = supportConfig.extraBufferCapacity,
            onBufferOverflow = supportConfig.onBufferOverflow
        )
    }

    // for any data flow
    override val anyDataFlow: Flow<*>? = null
    override val resultFlow: Flow<*>? = null


    // for pages
    override val selectedPage: MutableStateFlow<Int> by unsafeLazy { MutableStateFlow(0) }
    override val toolbarSubTitle: MutableStateFlow<String>? = null
    override val toolbarTitle: MutableStateFlow<String>? = null
    override val toolbarMenu: MutableStateFlow<Int>? = null

    override fun processEventAsync(viewEvent: com.rasalexman.sresult.data.dto.ISEvent) {
        processEvent(viewEvent)
    }

    override fun processEvent(viewEvent: com.rasalexman.sresult.data.dto.ISEvent) {
        eventsFlow.tryEmit(viewEvent)
    }

    override fun handleErrorState(errorResult: com.rasalexman.sresult.data.dto.SResult.AbstractFailure) {
        supportFlow.tryEmit(createFailure(errorResult))
    }

    /**
     * When need to go back from layout
     * Don't forget to set canGoBack in Fragment to true
     */
    override fun onBackClicked() {
        navigationFlow.tryEmit(navigateBackResult())
    }

    override fun clear() {
        superVisorJob.cancelChildren()
        clearEventsFlow()
    }

    protected open fun clearEventsFlow() {
        eventsFlow.tryEmit(com.rasalexman.sresult.data.dto.SEvent.Empty)
    }
}