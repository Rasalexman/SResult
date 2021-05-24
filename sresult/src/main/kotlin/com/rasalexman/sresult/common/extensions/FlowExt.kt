package com.rasalexman.sresult.common.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Запускает flow на IO потоке
 *
 * @param flowBlock блок, выполняемый в flow, с литералом FlowCollector
 */
fun <T> ioFlow(flowBlock: suspend FlowCollector<T>.() -> Unit) = flow(flowBlock)
    .flowOn(Dispatchers.IO)

/**
 * Запускает flow на IO потоке как [ioFlow], в случае ошибки будет заэмичено [defaultValue]
 *
 * @param defaultValue дефолтное значение, эмитится в flow, при ошибке
 * @param flowBlock блок, выполняемый в flow, с литералом FlowCollector
 */
fun <T> safeIoFlow(defaultValue: T? = null, flowBlock: suspend FlowCollector<T>.() -> Unit) =
    ioFlow(flowBlock)
        .catch { throwable ->
            logg { throwable.message }
            defaultValue?.let { emit(it) }
        }