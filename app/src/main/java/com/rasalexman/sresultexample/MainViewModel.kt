package com.rasalexman.sresultexample

import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.rasalexman.sresult.common.extensions.progressResult
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.extensions.toastResult
import com.rasalexman.sresult.common.typealiases.FlowResult
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.extensions.onEventFlowResult
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MainViewModel : BaseViewModel() {

    companion object {
        private const val PROGRESS_DELAY = 200L
    }

    val stateViewModel = liveData<SResult<UserModel>> {
        emitSource(loadWithProgress().flowOn(Dispatchers.IO).asLiveData())
    }

    val eventFetchCatcher = onEventFlowResult<SEvent.Fetch, UserModel> {
        emit(progressResult(10))
        delay(PROGRESS_DELAY)

        emit(progressResult(50))
        delay(PROGRESS_DELAY)
        val user = UserModel(
            name = "Alex",
            email = "sphc@yandex.ru"
        )
        emit(user.toSuccessResult())
    }

    private fun loadWithProgress(): FlowResult<UserModel> = flow<SResult<UserModel>> {
        emit(progressResult(10))
        delay(PROGRESS_DELAY)
        emit(progressResult(40))
        delay(PROGRESS_DELAY)
        emit(toastResult(message = "Hello World"))
        val user = UserModel(
            name = "Alex",
            email = "sphc@yandex.ru"
        )
        emit(user.toSuccessResult())
    }
}