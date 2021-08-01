package com.rasalexman.sresult.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * Запускает flow на IO потоке
 *
 * @param flowBlock блок, выполняемый в flow, с литералом FlowCollector
 */
@Suppress("EXPERIMENTAL_API_USAGE")
fun <T> ioFlow(flowBlock: suspend FlowCollector<T>.() -> Unit) = flow(flowBlock)
    .flowOn(Dispatchers.IO)

/**
 * Запускает flow на IO потоке как [ioFlow], в случае ошибки будет заэмичено [defaultValue]
 *
 * @param defaultValue дефолтное значение, эмитится в flow, при ошибке
 * @param flowBlock блок, выполняемый в flow, с литералом FlowCollector
 */
@Suppress("EXPERIMENTAL_API_USAGE")
fun <T> safeIoFlow(defaultValue: T? = null, flowBlock: suspend FlowCollector<T>.() -> Unit) =
    ioFlow(flowBlock)
        .catch { throwable ->
            logg { throwable.message }
            defaultValue?.let { emit(it) }
        }

fun <T : Any> Flow<T>.asState(
    scope: CoroutineScope,
    initialValue: T,
    started: SharingStarted = SharingStarted.WhileSubscribed()
): StateFlow<T> {
    return this.stateIn(scope, started, initialValue)
}