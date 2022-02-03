package com.rasalexman.sresultexample.state

import androidx.lifecycle.asLiveData
import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresultexample.NavigationMainDirections
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.extensions.launchUITryCatch
import com.rasalexman.sresultpresentation.extensions.onEventFlow
import com.rasalexman.sresultpresentation.viewModels.flowable.FlowableViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.*

class StateFlowViewModel : FlowableViewModel() {

    override val toolbarTitle: MutableStateFlow<String> = MutableStateFlow(string(R.string.title_state_flow))

    override val resultFlow by unsafeLazy {
        onEventFlow<SEvent.Fetch, AnyResult> {
            emit(loadingResult())
            delay(3000L)
            val toastText = stringSuspend(R.string.toast_state)
            val result = toastResult(toastText)
            emit(result)
        }
    }

    override val anyDataFlow: Flow<String> by unsafeLazy {
        resultFlow.combine(supportFlow) { r, s ->
            println("------> anyDataFlow combined = $r | $s")
            val text = r.takeIf { r.isToast }?.run {
                "result is Toast | support = ${s.data}"
            }.orIfEmpty { string(R.string.app_name) }
            text
        }.flowOn(Dispatchers.Default).filter { it.isNotEmpty() }
    }

    val description by unsafeLazy {
        anyDataFlow.asLiveData()
    }

    fun onDoActionClicked() {
        processEvent(SEvent.Fetch)
    }

    fun onSuccessClicked() = launchUITryCatch {
        supportFlow.tryEmit(loadingResult())
        delay(2000L)
        supportFlow.tryEmit(successResult(UUID.randomUUID().toString().take(5)))
    }

    fun onShowAnotherFragment() = launchUITryCatch {
        supportFlow.tryEmit(loadingResult())
        delay(2000L)
        navigationFlow.tryEmit(NavigationMainDirections.showEmptyFragment().toNavigateResult())
    }
}