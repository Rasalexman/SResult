package com.rasalexman.sresultpresentation.extensions

import androidx.lifecycle.*
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.toErrorResult
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.ISResult
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

fun BaseViewModel.launchUITryCatch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    catchBlock: ((Throwable) -> Unit)? = null, tryBlock: suspend CoroutineScope.() -> Unit
) {
    try {
        viewModelScope.launch(viewModelScope.coroutineContext, start, tryBlock)
    } catch (e: Throwable) {
        catchBlock?.invoke(e) ?: handleErrorState(e.toErrorResult())
    }
}

fun BaseViewModel.launchAsyncTryCatch(catchBlock: ((Throwable) -> Unit)? = null, tryBlock: suspend CoroutineScope.() -> Unit) {
    try {
        launchAsync(CoroutineStart.DEFAULT, tryBlock)
    } catch (e: Throwable) {
        catchBlock?.invoke(e) ?: handleErrorState(e.toErrorResult())
    }
}

fun BaseViewModel.launchAsync(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO, start, block)
}

inline fun <reified T> BaseViewModel.asyncLiveData(
    noinline block: suspend LiveDataScope<T>.() -> Unit
) = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO, block = block)

/**
 *
 */
inline fun <reified E : ISEvent> BaseViewModel.onEventResult(
    crossinline block: suspend LiveDataScope<ISResult<*>>.(E) -> Unit
): LiveData<ISResult<*>> {
    return eventLiveData.switchMap { event ->
        asyncLiveData {
            (event as? E)?.let {
                try {
                    block(event)
                } catch (e: Exception) {
                    emit(SResult.ErrorResult.Error(exception = e))
                }
            }
        }
    }
}

/**
 *
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEvent(
    isDistincted: Boolean = false,
    autoObserved: Boolean = false,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
): LiveData<T> {
    val eld = if(isDistincted) eventLiveData.distinctUntilChanged() else eventLiveData
    val eventLV: LiveData<T> = eld.switchMap { event ->
        asyncLiveData {
            (event as? E)?.let {
                try {
                    block(event)
                } catch (e: Exception) {
                    loggE(e, "There is an exception in ${this@onEvent}")
                    (this as? LiveDataScope<SResult<*>>)?.apply {
                        emit(SResult.ErrorResult.Error(exception = e))
                    }
                }
            }
            //event.applyIfSuspend(event is E) { this.block(event as E) }
        }
    }
    if (autoObserved) {
        liveDataToObserve.add(eventLV)
    }

    return eventLV
}

@Suppress("UNCHECKED_CAST")
inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventMutable(
    isDistincted: Boolean = false,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
): MutableLiveData<T> {
    val eld = if(isDistincted) eventLiveData.distinctUntilChanged() else eventLiveData
    return eld.mutableSwitchMap { event ->
        asyncLiveData<T> {
            (event as? E)?.let {
                try {
                    block(event)
                } catch (e: Exception) {
                    loggE(e, "There is an exception in ${this@onEventMutable}")
                    (this as? LiveDataScope<SResult<*>>)?.apply {
                        emit(SResult.ErrorResult.Error(exception = e))
                    }
                }
            }
        }
    }
}