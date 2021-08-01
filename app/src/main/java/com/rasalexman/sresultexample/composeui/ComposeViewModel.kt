package com.rasalexman.sresultexample.composeui

import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.extensions.onEventFlow
import com.rasalexman.sresultpresentation.viewModels.flowable.FlowableViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.random.Random

class ComposeViewModel : FlowableViewModel() {

    override val toolbarTitle: MutableStateFlow<String> = MutableStateFlow(string(R.string.title_state_flow))

    override val resultFlow: Flow<SResult<String>> by unsafeLazy {
        onEventFlow<SEvent.Fetch, SResult<String>> {
            emit(loadingResult())
            delay(2000L)
            val rnd = Random.nextInt(10, 40)
            val generated = UUID.randomUUID().toString().take(rnd)
            emit(generated.toSuccessResult())
        }
    }

    val loadingText by unsafeLazy {
        resultFlow.map {
            it.collectDataIfSuccess(string(R.string.hint_loading)).orEmpty()
        }
    }

    val loadingVisibility: Flow<Float> by unsafeLazy {
        resultFlow.map {
            it.flatMapDataIfNotSuccess { 1f }.orZero()
        }
    }

    fun onClickGenerate() {
        processEvent(SEvent.Fetch)
    }
}