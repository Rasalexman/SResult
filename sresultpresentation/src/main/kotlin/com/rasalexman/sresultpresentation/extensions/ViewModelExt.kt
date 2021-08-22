@file:Suppress("unused")

package com.rasalexman.sresultpresentation.extensions

import androidx.lifecycle.*
import com.rasalexman.sresult.common.extensions.errorResult
import com.rasalexman.sresult.common.extensions.toErrorResult
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.viewModels.BaseContextViewModel
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import com.rasalexman.sresultpresentation.viewModels.IEventableViewModel
import com.rasalexman.sresultpresentation.viewModels.flowable.FlowableViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun BaseContextViewModel.launchUITryCatch(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    catchBlock: ((Throwable) -> Unit)? = null, tryBlock: suspend CoroutineScope.() -> Unit
) {
    try {
        viewModelScope.launch(
            viewModelScope.coroutineContext + dispatcher + superVisorJob,
            start,
            tryBlock
        )
    } catch (e: Throwable) {
        catchBlock?.invoke(e) ?: handleErrorState(e.toErrorResult())
    }
}

fun BaseContextViewModel.launchAsyncTryCatch(
    catchBlock: ((Throwable) -> Unit)? = null,
    tryBlock: suspend CoroutineScope.() -> Unit
) {
    try {
        launchAsync(block = tryBlock)
    } catch (e: Throwable) {
        catchBlock?.invoke(e) ?: handleErrorState(e.toErrorResult())
    }
}

fun BaseContextViewModel.launchAsync(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(
        viewModelScope.coroutineContext + dispatcher + superVisorJob,
        start,
        block
    )
}

inline fun <reified T> BaseContextViewModel.asyncLiveData(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    noinline block: suspend LiveDataScope<T>.() -> Unit
) = liveData(context = viewModelScope.coroutineContext + dispatcher + superVisorJob, block = block)

inline fun <reified T> BaseContextViewModel.asyncMutableLiveData(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    noinline block: suspend LiveDataScope<T>.() -> Unit
): MutableLiveData<T> = asyncLiveData(dispatcher = dispatcher, block = block) as MutableLiveData<T>

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified E : ISEvent, reified T : Any?> LiveDataScope<T>.processEventsCollect(
    isDistincted: Boolean = false,
    viewModelScope: CoroutineScope,
    eventDelay: Long = 0L,
    eventLiveData: MutableLiveData<ISEvent>,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
) {
    val eld = if (isDistincted) eventLiveData.asFlow().distinctUntilChanged() else eventLiveData.asFlow()
    eld.stateIn(viewModelScope).collect { event ->
        if(event !is SEvent.Empty && event is E) {
            try {
                if (eventDelay > 0) {
                    delay(eventDelay)
                }
                block(event)
                eventLiveData.postValue(SEvent.Empty)
            } catch (e: Exception) {
                //loggE(e, "There is an exception in ${this@onEvent}")
                (this as? LiveDataScope<SResult<*>>)?.apply {
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
inline fun <reified E : ISEvent, reified T : Any?> BaseViewModel.onEvent(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    autoObserved: Boolean = false,
    eventDelay: Long = 0L,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
): LiveData<T> {
    val eventLV: LiveData<T> = asyncLiveData(dispatcher = dispatcher) {
        processEventsCollect(
            isDistincted = isDistincted,
            viewModelScope = this@onEvent.viewModelScope,
            eventDelay = eventDelay,
            eventLiveData = eventLiveData,
            block = block
        )
    }
    if (autoObserved) {
        liveDataToObserve.add(eventLV)
    }

    return eventLV
}

/**
 *
 */
inline fun <reified E : ISEvent> BaseViewModel.onEventResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    autoObserved: Boolean = false,
    eventDelay: Long = 0L,
    crossinline block: suspend LiveDataScope<SResult<*>>.(E) -> Unit
): LiveData<SResult<*>> {
    val eventLV: LiveData<SResult<*>> = asyncLiveData(dispatcher = dispatcher) {
        processEventsCollect(
            isDistincted = isDistincted,
            viewModelScope = this@onEventResult.viewModelScope,
            eventDelay = eventDelay,
            eventLiveData = eventLiveData,
            block = block
        )
    }
    if (autoObserved) {
        liveDataToObserve.add(eventLV)
    }
    return eventLV
}



@Suppress("UNCHECKED_CAST")
inline fun <reified E : ISEvent, reified T : Any?> BaseViewModel.onEventMutable(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0L,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
): MutableLiveData<T> {
    return asyncMutableLiveData(dispatcher = dispatcher) {
        processEventsCollect(
            isDistincted = isDistincted,
            viewModelScope = this@onEventMutable.viewModelScope,
            eventDelay = eventDelay,
            eventLiveData = eventLiveData,
            block = block
        )
    }
}

inline fun <reified E : ISEvent, reified T : Any> IEventableViewModel.createEventFlow(
    eventFlow: Flow<ISEvent>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit,
    noinline catchBlock: ((Throwable) -> Unit)? = null
): Flow<T> {
    return flow {
        eventFlow.collect {
            if (it is E) {
                if (eventDelay > 0) {
                    delay(eventDelay)
                }
                this@flow.block(it)
            }
        }
    }.flowOn(dispatcher).apply {
        emitOnStart?.let { startAction ->
            onStart {
                emit(startAction())
            }
        }
    }.catch {
        catchBlock?.invoke(it)
    }
}

inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit
): LiveData<T> {
    val eventFlow = eventLiveData.asFlow()
    val currentEventFlow = if (isDistincted) eventFlow.distinctUntilChanged() else eventFlow

    return createEventFlow(
        eventFlow = currentEventFlow,
        dispatcher = dispatcher,
        eventDelay = eventDelay,
        emitOnStart = emitOnStart,
        block = block
    ) {
        supportLiveData.postValue(errorResult(exception = it))
    }.asLiveData(dispatcher)
}

inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventMutableFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit
): LiveData<T> {
    return onEventFlow(dispatcher, isDistincted, eventDelay, emitOnStart, block) as MutableLiveData<T>
}

inline fun <reified E : ISEvent, reified T : Any> FlowableViewModel.onEventFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit
): Flow<T> {
    val currentEventFlow = if (isDistincted) eventsFlow.distinctUntilChanged()
    else eventsFlow

    return createEventFlow(
        eventFlow = currentEventFlow,
        dispatcher = dispatcher,
        eventDelay = eventDelay,
        emitOnStart = emitOnStart,
        block = block
    ) {
        supportFlow.tryEmit(errorResult(exception = it))
    }
}

inline fun <reified T : Any> BaseViewModel.onAnyEventFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(ISEvent) -> Unit
): LiveData<T> = onEventFlow(dispatcher, isDistincted, eventDelay, emitOnStart, block)


inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventFlowResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> SResult<T>)? = null,
    crossinline block: suspend FlowCollector<SResult<T>>.(E) -> Unit
): LiveData<SResult<T>> = onEventFlow(dispatcher, isDistincted, eventDelay, emitOnStart, block)

inline fun <reified T : Any> BaseViewModel.onAnyEventFlowResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> SResult<T>)? = null,
    crossinline block: suspend FlowCollector<SResult<T>>.(ISEvent) -> Unit
): LiveData<SResult<T>> = onEventFlow(dispatcher, isDistincted, eventDelay, emitOnStart, block)

inline fun <reified E : ISEvent> BaseViewModel.onEventFlowAnyResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> AnyResult)? = null,
    crossinline block: suspend FlowCollector<AnyResult>.(E) -> Unit
): LiveData<AnyResult> = onEventFlow(dispatcher, isDistincted, eventDelay, emitOnStart, block)

fun BaseViewModel.onAnyEventFlowAnyResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistincted: Boolean = false,
    eventDelay: Long = 0,
    emitOnStart: (() -> AnyResult)? = null,
    block: suspend FlowCollector<AnyResult>.(ISEvent) -> Unit
): LiveData<AnyResult> = onEventFlow(dispatcher, isDistincted, eventDelay, emitOnStart, block)

