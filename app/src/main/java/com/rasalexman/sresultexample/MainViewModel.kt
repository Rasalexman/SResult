package com.rasalexman.sresultexample

import com.rasalexman.sresult.common.extensions.loadingResult
import com.rasalexman.sresult.common.extensions.logg
import com.rasalexman.sresult.common.extensions.progressResult
import com.rasalexman.sresult.common.typealiases.AnyResultMutableLiveData
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresultpresentation.extensions.onEventFlowAnyResult
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.delay
import java.util.*
import kotlin.random.Random

class MainViewModel : BaseViewModel() {

    companion object {
        private const val PROGRESS_DELAY = 1200L
    }

    override val resultLiveData = onEventFlowAnyResult<SEvent.Fetch> {
        logg { "-------> resultLiveData event is $it" }

        emit(loadingResult())

        emit(progressResult(10))
        delay(PROGRESS_DELAY)

        emit(progressResult(50))
        delay(PROGRESS_DELAY)
        val user = UserModel(
            name = "Alex",
            email = "sphc@yandex.ru",
            token = UUID.randomUUID().toString()
        )
        emit(progressResult(70))

        emit(UserAuthState.Success(user))
    }

    override val supportLiveData = onEventFlowAnyResult<SEvent.Refresh> {
        logg { "-------> supportLiveData event is $it" }
        val rand = Random.nextInt(10, 54)
        emit(progressResult(rand))
    } as AnyResultMutableLiveData

    fun onProfileClicked() {
        val rand = Random.nextInt(10, 54)
        val event: ISEvent = if(rand % 2 == 0) SEvent.Fetch else SEvent.Refresh
        processViewEvent(event)
    }
}