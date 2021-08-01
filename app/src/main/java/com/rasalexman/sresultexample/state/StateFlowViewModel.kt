package com.rasalexman.sresultexample.state

import androidx.lifecycle.viewModelScope
import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresultexample.MainFragmentDirections
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.extensions.launchUITryCatch
import com.rasalexman.sresultpresentation.extensions.onEventFlow
import com.rasalexman.sresultpresentation.viewModels.flowable.FlowableViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.*

class StateFlowViewModel : FlowableViewModel() {

    override val toolbarTitle: MutableStateFlow<String> = MutableStateFlow(string(R.string.title_state_flow))

    override val resultFlow by unsafeLazy {
        onEventFlow<SEvent.Fetch, AnyResult> {
            emit(loadingResult())
            delay(1000L)
            val toastText = stringSuspend(R.string.toast_state)
            val result = toastResult(toastText)
            emit(result)
        }
    }

    val isLoadingInvisible: StateFlow<Boolean> by unsafeLazy {
        resultFlow.map { result ->
            println("------> isLoadingInvisible = $result")
            !result.isLoading
        }.asState(viewModelScope, true)
    }

    override val anyDataFlow: Flow<*> by unsafeLazy {
        resultFlow.combine(supportFlow) { r, s ->
            println("------> anyDataFlow combined = $r | $s")
            val text = r.takeIf { r.isToast }?.run {
                "result is Toast | support = $s"
            }.orEmpty()
            text
        }.filter { it.isNotEmpty() }
    }

    fun onDoActionClicked() {
        processEvent(SEvent.Fetch)
    }

    fun onSuccessClicked() = launchUITryCatch {
        supportFlow.tryEmit(loadingResult())
        delay(1000L)
        supportFlow.tryEmit(successResult(UUID.randomUUID().toString().take(5)))
    }

    fun onShowAnotherFragment() = launchUITryCatch {
        supportFlow.tryEmit(loadingResult())
        delay(2000L)
        navigationFlow.tryEmit(MainFragmentDirections.showEmptyFragment().toNavigateResult())
    }
}