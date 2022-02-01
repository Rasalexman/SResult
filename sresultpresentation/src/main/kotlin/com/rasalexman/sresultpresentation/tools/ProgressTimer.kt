package com.rasalexman.sresultpresentation.tools

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

fun startProgressTimer(
    coroutineScope: CoroutineScope,
    timerIntervalMs: Long = 1000L,
    elapsedTime: MutableLiveData<Long>? = null,
    remainingTime: MutableLiveData<Long>? = null,
    timeoutInSec: Int? = null,
    hideProgress: (() -> Unit)? = null,
    handleFailure: ((String) -> Unit)? = null

): Job {

    val startTime = System.currentTimeMillis()
    val timeOutInMills = timeoutInSec?.times(timerIntervalMs) ?: 0L

    elapsedTime?.postValue(0)

    timeoutInSec?.let {
        remainingTime?.postValue(timeOutInMills)
    }

    return coroutineScope.timer(timerIntervalMs) {
        if(coroutineScope.isActive) {
            val elapsed = System.currentTimeMillis() - startTime
            elapsedTime?.postValue(elapsed)

            timeoutInSec?.let {
                if (elapsed > timeOutInMills) {
                    coroutineScope.cancel()
                    hideProgress?.invoke()
                    handleFailure?.invoke("TimeOutError")
                }

                remainingTime?.postValue(timeOutInMills - elapsed)
            }
        }
    }
}