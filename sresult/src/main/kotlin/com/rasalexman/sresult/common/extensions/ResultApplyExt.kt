@file:Suppress("UNCHECKED_CAST", "REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE", "unused")
package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.*
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.flow.onEach



suspend inline fun <reified I : Any> SResult<I>.applyIfSuccessSuspend(crossinline block: SInHandler<I>): SResult<I> {
    if (this is SResult.Success) block(this.data)
    return this
}


suspend inline fun <reified I : Any> FlowResult<I>.applyIfFlowSuccessSuspend(crossinline block: SInHandler<I>): FlowResult<I> {
    return this.onEach { result ->
        if (result is SResult.Success) block(result.data)
    }
}


suspend inline fun <reified I : Any> SResult<I>.applyIfEmptySuspend(crossinline block: SUnitHandler): SResult<I> {
    if (this is SResult.Empty) block()
    return this
}

suspend inline fun <reified I : Any> FlowResult<I>.applyIfFlowEmptySuspend(crossinline block: SUnitHandler): FlowResult<I> {
    return this.onEach { result ->
        if (result is SResult.Empty) block()
    }
}


suspend inline fun <reified I : Any> SResult<I>.applyIfNothingSuspend(crossinline block: SUnitHandler): SResult<I> {
    if (this is SResult.NothingResult) block()
    return this
}

suspend inline fun <reified I : Any> FlowResult<I>.applyIfFlowNothingSuspend(crossinline block: SUnitHandler): FlowResult<I> {
    return this.onEach { result ->
        if (result is SResult.NothingResult) block()
    }
}


suspend inline fun <reified I : Any> SResult<I>.applyIfErrorSuspend(crossinline block: SInHandler<SResult.AbstractFailure>): SResult<I> {
    if (this is SResult.AbstractFailure) block(this)
    return this
}

suspend inline fun <reified I : Any> FlowResult<I>.applyIfFlowErrorSuspend(crossinline block: SInHandler<SResult.AbstractFailure>): FlowResult<I> {
    return this.onEach { result ->
        if (result is SResult.AbstractFailure) block(result)
    }
}


// /--- Inline Applying functions
inline fun <reified I : Any> SResult<I>.applyIfSuccess(block: InHandler<I>): SResult<I> {
    if (this is SResult.Success) block(this.data)
    return this
}

inline fun <reified I : Any> FlowResult<I>.applyIfFlowSuccess(crossinline block: InHandler<I>): FlowResult<I> {
    return this.onEach {
        if (it is SResult.Success) block(it.data)
    }
}

// /--- Inline Applying functions
inline fun <reified I> SResult<*>.applyIfSuccessTyped(block: InHandler<I>): SResult<*> {
    if (this is SResult.Success && this.data is I) block(this.data as I)
    return this
}

inline fun <reified I : Any> SResult<I>.applyIfError(block: InHandler<SResult.AbstractFailure>): SResult<I> {
    if (this is SResult.AbstractFailure) block(this)
    return this
}

inline fun <reified I : Any> FlowResult<I>.applyIfFlowError(crossinline block: InHandler<SResult.AbstractFailure>): FlowResult<I> {
    return this.onEach {
        if (it is SResult.AbstractFailure) block(it)
    }
}

inline fun <reified I : SResult<*>> SResult<*>.applyIfType(block: I.() -> Unit): SResult<*> {
    if (this::class == I::class) block(this as I)
    return this
}


suspend inline fun <reified I : SResult<*>> SResult<*>.applyIfTypeSuspend(crossinline block: suspend I.() -> Unit): SResult<*> {
    if (this::class == I::class) block(this as I)
    return this
}