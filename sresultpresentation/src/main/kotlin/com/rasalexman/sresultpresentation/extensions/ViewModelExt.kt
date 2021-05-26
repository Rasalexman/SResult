package com.rasalexman.sresultpresentation.extensions

import androidx.lifecycle.*
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.toErrorResult
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.ISResult
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.*

fun BaseViewModel.launchUITryCatch(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    catchBlock: ((Throwable) -> Unit)? = null, tryBlock: suspend CoroutineScope.() -> Unit
) {
    try {
        viewModelScope.launch(viewModelScope.coroutineContext + dispatcher, start, tryBlock)
    } catch (e: Throwable) {
        catchBlock?.invoke(e) ?: handleErrorState(e.toErrorResult())
    }
}

fun BaseViewModel.launchAsyncTryCatch(catchBlock: ((Throwable) -> Unit)? = null, tryBlock: suspend CoroutineScope.() -> Unit) {
    try {
        launchAsync(block = tryBlock)
    } catch (e: Throwable) {
        catchBlock?.invoke(e) ?: handleErrorState(e.toErrorResult())
    }
}

fun BaseViewModel.launchAsync(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(viewModelScope.coroutineContext + dispatcher, start, block)
}

inline fun <reified T> BaseViewModel.asyncLiveData(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    noinline block: suspend LiveDataScope<T>.() -> Unit
) = liveData(context = viewModelScope.coroutineContext + dispatcher, block = block)

/**
 *
 */
inline fun <reified E : ISEvent> BaseViewModel.onEventResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline block: suspend LiveDataScope<ISResult<*>>.(E) -> Unit
): LiveData<ISResult<*>> {
    return eventLiveData.switchMap { event ->
        asyncLiveData(dispatcher = dispatcher) {
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
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    autoObserved: Boolean = false,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
): LiveData<T> {
    val eld = if(isDistincted) eventLiveData.distinctUntilChanged() else eventLiveData
    val eventLV: LiveData<T> = eld.switchMap { event ->
        asyncLiveData(dispatcher = dispatcher) {
            (event as? E)?.let {
                try {
                    block(event)
                } catch (e: Exception) {
                    loggE(e, "There is an exception in ${this@onEvent}")
                    (this as? LiveDataScope<ISResult<*>>)?.apply {
                        emit(SResult.ErrorResult.Error(exception = e))
                    }
                }
            }
        }
    }
    if (autoObserved) {
        liveDataToObserve.add(eventLV)
    }

    return eventLV
}

@Suppress("UNCHECKED_CAST")
inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventMutable(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
): MutableLiveData<T> {
    val eld = if(isDistincted) eventLiveData.distinctUntilChanged() else eventLiveData
    return eld.mutableSwitchMap { event ->
        asyncLiveData<T>(dispatcher = dispatcher) {
            (event as? E)?.let {
                try {
                    block(event)
                } catch (e: Exception) {
                    loggE(e, "There is an exception in ${this@onEventMutable}")
                    (this as? LiveDataScope<ISResult<*>>)?.apply {
                        emit(SResult.ErrorResult.Error(exception = e))
                    }
                }
            }
        }
    }
}