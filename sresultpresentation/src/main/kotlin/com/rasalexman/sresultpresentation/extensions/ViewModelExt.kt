package com.rasalexman.sresultpresentation.extensions

import androidx.lifecycle.*
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.toErrorResult
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
    crossinline block: suspend LiveDataScope<SResult<*>>.(E) -> Unit
): LiveData<SResult<*>> {
    return eventLiveData.switchMap { event ->
        asyncLiveData(dispatcher = dispatcher) {
            (event as? E)?.let {
                try {
                    block(event)
                } catch (e: Exception) {
                    emit(SResult.AbstractFailure.Error(exception = e))
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
                    (this as? LiveDataScope<SResult<*>>)?.apply {
                        emit(SResult.AbstractFailure.Error(exception = e))
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
                    (this as? LiveDataScope<SResult<*>>)?.apply {
                        emit(SResult.AbstractFailure.Error(exception = e))
                    }
                }
            }
        }
    }
}

inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    asMutable: Boolean = false,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit
): LiveData<T> {
    return flow<T> {
        eventLiveData.asFlow().collect {
            if(it is E) {
                if(eventDelay > 0) {
                    delay(eventDelay)
                }
                this@flow.block(it)
            }
        }
    }.flowOn(dispatcher).apply {
        if(isDistincted) distinctUntilChanged()

        emitOnStart?.let { startAction ->
            onStart {
                emit(startAction())
            }
        }
    }.run {
        if(asMutable) {
            asLiveData() as MutableLiveData<T>
        } else {
            asLiveData()
        }
    }
}

inline fun <reified T : Any> BaseViewModel.onAnyEventFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    asMutable: Boolean = false,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(ISEvent) -> Unit
): LiveData<T> = onEventFlow<ISEvent, T>(dispatcher, isDistincted, eventDelay, asMutable, emitOnStart, block)


inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventFlowResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    asMutable: Boolean = false,
    noinline emitOnStart: (() -> SResult<T>)? = null,
    crossinline block: suspend FlowCollector<SResult<T>>.(E) -> Unit
): LiveData<SResult<T>> = onEventFlow<E, SResult<T>>(dispatcher, isDistincted, eventDelay, asMutable, emitOnStart, block)

inline fun <reified T : Any> BaseViewModel.onAnyEventFlowResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    asMutable: Boolean = false,
    noinline emitOnStart: (() -> SResult<T>)? = null,
    crossinline block: suspend FlowCollector<SResult<T>>.(ISEvent) -> Unit
): LiveData<SResult<T>> = onEventFlow<ISEvent, SResult<T>>(dispatcher, isDistincted, eventDelay, asMutable, emitOnStart, block)

inline fun <reified E : ISEvent> BaseViewModel.onEventFlowAnyResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    asMutable: Boolean = false,
    noinline emitOnStart: (() -> AnyResult)? = null,
    crossinline block: suspend FlowCollector<AnyResult>.(E) -> Unit
): LiveData<AnyResult> = onEventFlow<E, AnyResult>(dispatcher, isDistincted, eventDelay, asMutable, emitOnStart, block)

fun BaseViewModel.onAnyEventFlowAnyResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    asMutable: Boolean = false,
    emitOnStart: (() -> AnyResult)? = null,
    block: suspend FlowCollector<AnyResult>.(ISEvent) -> Unit
): LiveData<AnyResult> = onEventFlow<ISEvent, AnyResult>(dispatcher, isDistincted, eventDelay, asMutable, emitOnStart, block)

