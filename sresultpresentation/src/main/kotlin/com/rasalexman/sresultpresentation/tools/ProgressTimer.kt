package com.rasalexman.sresultpresentation.tools

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

fun startProgressTimer(
    coroutineScope: CoroutineScope,
    elapsedTime: MutableLiveData<Long>? = null,
    remainingTime: MutableLiveData<Long>? = null,
    timeoutInSec: Int? = null,
    hideProgress: (() -> Unit)? = null,
    handleFailure: ((String) -> Unit)? = null
): Job {

    val startTime = System.currentTimeMillis()
    val timeOutInMills = timeoutInSec?.times(1000L) ?: 0L

    elapsedTime?.postValue(0)

    timeoutInSec?.let {
        remainingTime?.postValue(timeOutInMills)
    }

    return coroutineScope.timer(1000) {
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