package com.rasalexman.sresultexample.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.rasalexman.sresult.common.extensions.loadingResult
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
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
        onEventFlow<SEvent.Fetch, SResult<String>> {
            emit(loadingResult())
            delay(1000L)
            val rnd = Random.nextInt(10, 40)
            val generated = UUID.randomUUID().toString().take(rnd)
            emit(generated.toSuccessResult())
        }
    }

    val generatedText: LiveData<String> by unsafeLazy {
        resultFlow.map { it.data.orEmpty() }.asLiveData()
    }

    fun onGenerateClicked() {
        processEvent(SEvent.Fetch)
    }
}