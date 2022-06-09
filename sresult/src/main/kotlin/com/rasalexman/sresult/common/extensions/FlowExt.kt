package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * Запускает flow на IO потоке
 *
 * @param flowBlock блок, выполняемый в flow, с литералом FlowCollector
 */
fun <T : Any> ioFlow(flowBlock: suspend FlowCollector<T>.() -> Unit) = flow(flowBlock)
    .flowOn(Dispatchers.IO)

/**
 * Запускает flow на IO потоке как [ioFlow], в случае ошибки будет заэмичено [defaultValue]
 *
 * @param defaultValue дефолтное значение, эмитится в flow, при ошибке
 * @param flowBlock блок, выполняемый в flow, с литералом FlowCollector
 */
fun <T : Any> safeIoFlow(defaultValue: T? = null, flowBlock: suspend FlowCollector<T>.() -> Unit) =
    ioFlow(flowBlock)
        .catch { throwable ->
            loggE(throwable)
            defaultValue?.let { emit(it) }
        }

/**
 * Запускает flow на IO потоке как [ioFlow]
 */
fun<T : Any> safeIoResultFlow(flowBlock: suspend FlowCollector<com.rasalexman.sresult.data.dto.SResult<T>>.() -> Unit) =
    ioFlow(flowBlock).catch { throwable ->
        loggE(throwable)
        emit(errorResult(exception = throwable))
    }

/**
 *
 */
fun <T : Any> Flow<T>.asState(
    scope: CoroutineScope,
    initialValue: T,
    started: SharingStarted = SharingStarted.WhileSubscribed()
): StateFlow<T> {
    return this.stateIn(scope, started, initialValue)
}

/**
 * Taken from the kotlin coroutine library
 * Stops further execution of the coroutine while [predicate] == false
 * */
suspend fun <T> Flow<T>.collectWhile(predicate: (T) -> Boolean) {
    try {
        collect {
            if (!predicate(it)) {
                throw InterruptedException()
            }
        }
    } catch (e : Exception) {
        if(e is CancellationException) {
            throw e
        }
    }
}