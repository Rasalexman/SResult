package com.rasalexman.sresultpresentation.viewModels

import com.rasalexman.sresult.common.extensions.navigateBackResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

open class BaseStateViewModel : BaseContextViewModel(), IStateViewModel {

    protected open val eventConfig: ISharedFlowConfig = SharedFlowConfig()
    protected open val supportConfig: ISharedFlowConfig = SharedFlowConfig()
    protected open val navigationConfig: ISharedFlowConfig = SharedFlowConfig()

    override val eventsFlow by unsafeLazy {
        MutableSharedFlow<ISEvent>(
            replay = eventConfig.replay,
            extraBufferCapacity = eventConfig.extraBufferCapacity,
            onBufferOverflow = eventConfig.onBufferOverflow
        )
    }
    override val navigationFlow by unsafeLazy {
        MutableSharedFlow<SResult.NavigateResult>(
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

    override fun processEventAsync(viewEvent: ISEvent) {
        processViewEvent(viewEvent)
    }

    override fun processViewEvent(viewEvent: ISEvent) {
        eventsFlow.tryEmit(viewEvent)
    }

    override fun handleErrorState(errorResult: SResult.AbstractFailure) {
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
        eventsFlow.tryEmit(SEvent.Empty)
    }
}