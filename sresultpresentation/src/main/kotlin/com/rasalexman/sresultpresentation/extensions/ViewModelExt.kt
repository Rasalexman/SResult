@file:Suppress("unused")

package com.rasalexman.sresultpresentation.extensions

import androidx.lifecycle.*
import com.rasalexman.sresult.common.extensions.errorResult
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.orIfNull
import com.rasalexman.sresult.common.extensions.toErrorResult
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.data.dto.ISEvent
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
    val exceptionHandler = CoroutineExceptionHandler { _, e ->
        catchBlock?.invoke(e) ?: handleErrorState(e.toErrorResult())
    }
    viewModelScope.launch(
        viewModelScope.coroutineContext + dispatcher + superVisorJob + exceptionHandler,
        start,
        tryBlock
    )
}

fun BaseContextViewModel.launchAsyncTryCatch(
    catchBlock: ((Throwable) -> Unit)? = null,
    tryBlock: suspend CoroutineScope.() -> Unit
) {
    val exceptionHandler = CoroutineExceptionHandler { _, e ->
        catchBlock?.invoke(e) ?: handleErrorState(e.toErrorResult())
    }
    launchAsync(exceptionHandler = exceptionHandler, block = tryBlock)
}

fun BaseContextViewModel.launchAsync(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    exceptionHandler: CoroutineExceptionHandler? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    val context = (viewModelScope.coroutineContext + dispatcher + superVisorJob).let {
        if (exceptionHandler != null) it + exceptionHandler else it
    }

    viewModelScope.launch(
        context,
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
    eventDelay: Long = 0L,
    eventFlow: Flow<ISEvent>,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
) {
    eventFlow.collect { event ->
        val currentEvent = (event as? E)
        currentEvent?.let {
            try {
                if (eventDelay > 0) {
                    delay(eventDelay)
                }
                block(it)
            } catch (e: Throwable) {
                loggE(e, "There is an exception in ${this@processEventsCollect}")
               /* (this as? LiveDataScope<SResult<*>>)?.apply {
                    emit(SResult.AbstractFailure.Error(exception = e))
                }*/
            }
        }
    }
}

/**
 *
 */
inline fun <reified E : ISEvent, reified T : Any?> BaseViewModel.onEvent(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    autoObserved: Boolean = false,
    eventDelay: Long = 0L,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
): LiveData<T> {
    val eld = if (isDistinct) eventLiveData.asFlow().distinctUntilChanged() else eventLiveData.asFlow()
    val eventLV: LiveData<T> = asyncLiveData(dispatcher = dispatcher) {
        processEventsCollect(
            eventDelay = eventDelay,
            eventFlow = eld,
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
    isDistinct: Boolean = false,
    autoObserved: Boolean = false,
    eventDelay: Long = 0L,
    crossinline block: suspend LiveDataScope<SResult<*>>.(E) -> Unit
): LiveData<SResult<*>> {
    val eld = if (isDistinct) eventLiveData.asFlow().distinctUntilChanged() else eventLiveData.asFlow()
    val eventLV: LiveData<SResult<*>> = asyncLiveData(dispatcher = dispatcher) {
        processEventsCollect(
            eventDelay = eventDelay,
            eventFlow = eld,
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
    isDistinct: Boolean = false,
    eventDelay: Long = 0L,
    crossinline block: suspend LiveDataScope<T>.(E) -> Unit
): MutableLiveData<T> {
    val eld = if (isDistinct) eventLiveData.asFlow().distinctUntilChanged() else eventLiveData.asFlow()
    return asyncMutableLiveData(dispatcher = dispatcher) {
        processEventsCollect(
            eventDelay = eventDelay,
            eventFlow = eld,
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
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit,
    noinline catchBlock: ((Throwable) -> Unit)? = null
): Flow<T> {
    val eventFlow = eventLiveData.asFlow()
    val currentEventFlow = if (isDistinct) eventFlow.distinctUntilChanged() else eventFlow

    return createEventFlow(
        eventFlow = currentEventFlow,
        dispatcher = dispatcher,
        eventDelay = eventDelay,
        emitOnStart = emitOnStart,
        block = block
    ) {
        catchBlock?.invoke(it).orIfNull {
            loggE(it)
            supportLiveData.postValue(errorResult(exception = it))
        }
    }
}

inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventLiveData(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit
): LiveData<T> {
    return onEventFlow(
        dispatcher = dispatcher,
        isDistinct = isDistinct,
        eventDelay = eventDelay,
        emitOnStart = emitOnStart,
        block = block
    ).asLiveData(dispatcher)
}

inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventMutableLiveData(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit
): LiveData<T> {
    return onEventLiveData(dispatcher, isDistinct, eventDelay, emitOnStart, block) as MutableLiveData<T>
}

inline fun <reified E : ISEvent, reified T : Any> FlowableViewModel.onEventFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(E) -> Unit
): Flow<T> {
    val currentEventFlow = if (isDistinct) eventsFlow.distinctUntilChanged()
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

inline fun <reified T : Any> BaseViewModel.onAnyEventLiveData(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> T)? = null,
    crossinline block: suspend FlowCollector<T>.(ISEvent) -> Unit
): LiveData<T> = onEventLiveData(dispatcher, isDistinct, eventDelay, emitOnStart, block)


inline fun <reified E : ISEvent, reified T : Any> BaseViewModel.onEventLiveDataResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> SResult<T>)? = null,
    crossinline block: suspend FlowCollector<SResult<T>>.(E) -> Unit
): LiveData<SResult<T>> = onEventLiveData(dispatcher, isDistinct, eventDelay, emitOnStart, block)

inline fun <reified T : Any> BaseViewModel.onAnyEventLiveDataResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> SResult<T>)? = null,
    crossinline block: suspend FlowCollector<SResult<T>>.(ISEvent) -> Unit
): LiveData<SResult<T>> = onEventLiveData(dispatcher, isDistinct, eventDelay, emitOnStart, block)

inline fun <reified E : ISEvent> BaseViewModel.onEventLiveDataAnyResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    noinline emitOnStart: (() -> AnyResult)? = null,
    crossinline block: suspend FlowCollector<AnyResult>.(E) -> Unit
): LiveData<AnyResult> = onEventLiveData(dispatcher, isDistinct, eventDelay, emitOnStart, block)

fun BaseViewModel.onAnyEventLiveDataAnyResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    isDistinct: Boolean = false,
    eventDelay: Long = 0,
    emitOnStart: (() -> AnyResult)? = null,
    block: suspend FlowCollector<AnyResult>.(ISEvent) -> Unit
): LiveData<AnyResult> = onEventLiveData(dispatcher, isDistinct, eventDelay, emitOnStart, block)

