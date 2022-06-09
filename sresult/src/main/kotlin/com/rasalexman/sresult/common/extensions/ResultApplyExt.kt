@file:Suppress("UNCHECKED_CAST", "REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE", "unused")
package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.*
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.flow.onEach



suspend inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.applyIfSuccessSuspend(crossinline block: SInHandler<I>): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.Success) block(this.data)
    return this
}


suspend inline fun <reified I : Any> FlowResult<I>.applyIfFlowSuccessSuspend(crossinline block: SInHandler<I>): FlowResult<I> {
    return this.onEach { result ->
        if (result is com.rasalexman.sresult.data.dto.SResult.Success) block(result.data)
    }
}


suspend inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.applyIfEmptySuspend(crossinline block: SUnitHandler): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.Empty) block()
    return this
}

suspend inline fun <reified I : Any> FlowResult<I>.applyIfFlowEmptySuspend(crossinline block: SUnitHandler): FlowResult<I> {
    return this.onEach { result ->
        if (result is com.rasalexman.sresult.data.dto.SResult.Empty) block()
    }
}


suspend inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.applyIfNothingSuspend(crossinline block: SUnitHandler): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.NothingResult) block()
    return this
}

suspend inline fun <reified I : Any> FlowResult<I>.applyIfFlowNothingSuspend(crossinline block: SUnitHandler): FlowResult<I> {
    return this.onEach { result ->
        if (result is com.rasalexman.sresult.data.dto.SResult.NothingResult) block()
    }
}


suspend inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.applyIfErrorSuspend(crossinline block: SInHandler<com.rasalexman.sresult.data.dto.SResult.AbstractFailure>): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(this)
    return this
}

suspend inline fun <reified I : Any> FlowResult<I>.applyIfFlowErrorSuspend(crossinline block: SInHandler<com.rasalexman.sresult.data.dto.SResult.AbstractFailure>): FlowResult<I> {
    return this.onEach { result ->
        if (result is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(result)
    }
}


// /--- Inline Applying functions
inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.applyIfSuccess(block: InHandler<I>): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.Success) block(this.data)
    return this
}

inline fun <reified I : Any> FlowResult<I>.applyIfFlowSuccess(crossinline block: InHandler<I>): FlowResult<I> {
    return this.onEach {
        if (it is com.rasalexman.sresult.data.dto.SResult.Success) block(it.data)
    }
}

// /--- Inline Applying functions
inline fun <reified I> com.rasalexman.sresult.data.dto.SResult<*>.applyIfSuccessTyped(block: InHandler<I>): com.rasalexman.sresult.data.dto.SResult<*> {
    if (this is com.rasalexman.sresult.data.dto.SResult.Success && this.data is I) block(this.data as I)
    return this
}

inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.applyIfError(block: InHandler<com.rasalexman.sresult.data.dto.SResult.AbstractFailure>): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(this)
    return this
}

inline fun <reified I : Any> FlowResult<I>.applyIfFlowError(crossinline block: InHandler<com.rasalexman.sresult.data.dto.SResult.AbstractFailure>): FlowResult<I> {
    return this.onEach {
        if (it is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(it)
    }
}

inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>> com.rasalexman.sresult.data.dto.SResult<*>.applyIfType(block: I.() -> Unit): com.rasalexman.sresult.data.dto.SResult<*> {
    if (this is I) block(this)
    return this
}


suspend inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>> com.rasalexman.sresult.data.dto.SResult<*>.applyIfTypeSuspend(crossinline block: suspend I.() -> Unit): com.rasalexman.sresult.data.dto.SResult<*> {
    if (this is I) block(this)
    return this
}

inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>> com.rasalexman.sresult.data.dto.SResult<*>.applyIfNotType(block: com.rasalexman.sresult.data.dto.SResult<*>.() -> Unit): com.rasalexman.sresult.data.dto.SResult<*> {
    if (this !is I) block(this)
    return this
}

suspend inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>> com.rasalexman.sresult.data.dto.SResult<*>.applyIfNotTypeSuspend(crossinline block: suspend com.rasalexman.sresult.data.dto.SResult<*>.() -> Unit): com.rasalexman.sresult.data.dto.SResult<*> {
    if (this !is I) block(this)
    return this
}