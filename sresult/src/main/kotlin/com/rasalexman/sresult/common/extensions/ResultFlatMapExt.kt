package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.FlowResult
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfSuccess(block: (I) -> SResult<O>): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowSuccess(crossinline block: (I) -> SResult<O>): FlowResult<O> {
    return this.map {
        if (it is SResult.Success) block(it.data)
        else it as SResult<O>
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfEmpty(block: () -> SResult<O>): SResult<O> {
    return if (this is SResult.Empty) block()
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowEmpty(crossinline block: () -> SResult<O>): FlowResult<O> {
    return this.map {
        if (it is SResult.Empty) block()
        else it as SResult<O>
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfSuccessSuspend(
    crossinline block: suspend (I) -> SResult<O>
): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowSuccessSuspend(
    crossinline block: suspend (I) -> SResult<O>
): FlowResult<O> {
    return this.map {
        if (it is SResult.Success) block(it.data)
        else it as SResult<O>
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfErrorSuspend(crossinline block: suspend (SResult.AbstractFailure) -> SResult<O>): SResult<O> {
    return if (this is SResult.AbstractFailure) block(this)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowErrorSuspend(crossinline block: suspend (SResult.AbstractFailure) -> SResult<O>): FlowResult<O> {
    return this.map {
        if (it is SResult.AbstractFailure) block(it)
        else it as SResult<O>
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfEmptySuspend(crossinline block: suspend () -> SResult<O>): SResult<O> {
    return if (this is SResult.Empty) block()
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowEmptySuspend(crossinline block: suspend () -> SResult<O>): FlowResult<O> {
    return this.map {
        if (it is SResult.Empty) block()
        else it as SResult<O>
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : SResult<*>, reified O : SResult<*>> SResult<*>.flatMapIfType(block: (I) -> O): O {
    return if (this::class == I::class) block(this as I)
    else this as O
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : SResult<*>, reified O : SResult<*>> SResult<*>.flatMapIfTypeSuspend(block:suspend (I) -> O): O {
    return if (this::class == I::class) block(this as I)
    else this as O
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : SResult<*>, reified O : SResult<*>> Flow<SResult<*>>.flatMapIfFlowTypeSuspend(
    crossinline block:suspend (I) -> O
): Flow<O> {
    return this.map {
        if (it::class == I::class) block(it as I)
        else it as O
    }
}