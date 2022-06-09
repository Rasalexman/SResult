package com.rasalexman.sresultexample.state

import androidx.lifecycle.asLiveData
import com.rasalexman.sresult.common.extensions.loadingResult
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.extensions.onEventFlow
import com.rasalexman.sresultpresentation.viewModels.flowable.FlowableViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.random.Random

class StateLayoutViewModel : FlowableViewModel() {

    override val toolbarTitle: MutableStateFlow<String> = MutableStateFlow(string(R.string.title_state_flow))

    override val resultFlow by unsafeLazy {
        onEventFlow<com.rasalexman.sresult.data.dto.SEvent.Fetch, com.rasalexman.sresult.data.dto.SResult<String>> {
            emit(loadingResult())
            delay(1000L)
            val rnd = Random.nextInt(10, 40)
            val generated = UUID.randomUUID().toString().take(rnd)
            emit(generated.toSuccessResult())
        }
    }

    val generatedText by unsafeLazy {
        resultFlow.map { it.data.orEmpty() }.asLiveData()
        // only with AGP 7.+
        //.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
    }

    fun onGenerateClicked() {
        processEvent(com.rasalexman.sresult.data.dto.SEvent.Fetch)
    }
}