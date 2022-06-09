@file:Suppress("UNCHECKED_CAST", "unused")

package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.FlowResult
import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapIfSuccess(block: (I) -> com.rasalexman.sresult.data.dto.SResult<O>): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Success) block(this.data)
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowSuccess(crossinline block: (I) -> com.rasalexman.sresult.data.dto.SResult<O>): FlowResult<O> {
    return this.map {
        it.flatMapIfSuccess(block)
    }
}


inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapIfEmpty(block: () -> com.rasalexman.sresult.data.dto.SResult<O>): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Empty) block()
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowEmpty(crossinline block: () -> com.rasalexman.sresult.data.dto.SResult<O>): FlowResult<O> {
    return this.map {
        it.flatMapIfEmpty(block)
    }
}


inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapIfError(crossinline block: (com.rasalexman.sresult.data.dto.SResult.AbstractFailure) -> com.rasalexman.sresult.data.dto.SResult<O>): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(this)
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowError(crossinline block: (com.rasalexman.sresult.data.dto.SResult.AbstractFailure) -> com.rasalexman.sresult.data.dto.SResult<O>): FlowResult<O> {
    return this.map {
        it.flatMapIfError(block)
    }
}


suspend inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapIfSuccessSuspend(
    crossinline block: suspend (I) -> com.rasalexman.sresult.data.dto.SResult<O>
): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Success) block(this.data)
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


suspend inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<*>.flatMapIfSuccessSuspendTyped(
    crossinline block: suspend (I) -> com.rasalexman.sresult.data.dto.SResult<O>
): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Success && data is I) block(this.data as I)
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowSuccessSuspend(
    crossinline block: suspend (I) -> com.rasalexman.sresult.data.dto.SResult<O>
): FlowResult<O> {
    return this.map {
        it.flatMapIfSuccessSuspend(block)
    }
}


suspend inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapIfErrorSuspend(crossinline block: suspend (com.rasalexman.sresult.data.dto.SResult.AbstractFailure) -> com.rasalexman.sresult.data.dto.SResult<O>): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(this)
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowErrorSuspend(
    crossinline block: suspend (com.rasalexman.sresult.data.dto.SResult.AbstractFailure) -> com.rasalexman.sresult.data.dto.SResult<O>
): FlowResult<O> {
    return this.map {
        it.flatMapIfErrorSuspend(block)
    }
}


suspend inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapIfEmptySuspend(crossinline block: suspend () -> com.rasalexman.sresult.data.dto.SResult<O>): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Empty) block()
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowEmptySuspend(
    crossinline block: suspend () -> com.rasalexman.sresult.data.dto.SResult<O>
): FlowResult<O> {
    return this.map {
        it.flatMapIfEmptySuspend(block)
    }
}


inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>> com.rasalexman.sresult.data.dto.SResult<*>.flatMapIfType(block: (I) -> com.rasalexman.sresult.data.dto.SResult<*>): com.rasalexman.sresult.data.dto.SResult<*> {
    return if (this::class == I::class) block(this as I)
    else this
}

inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>> com.rasalexman.sresult.data.dto.SResult<*>.flatMapIfNotType(block: (com.rasalexman.sresult.data.dto.SResult<*>) -> com.rasalexman.sresult.data.dto.SResult<*>): com.rasalexman.sresult.data.dto.SResult<*> {
    return if (this !is I) block(this)
    else this
}

inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<*>.flatMapIfSuccessTyped(
    block: (I) -> com.rasalexman.sresult.data.dto.SResult<O>
): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Success && this.data is I) block(this.data as I)
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


suspend inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>, reified O : com.rasalexman.sresult.data.dto.SResult<*>> com.rasalexman.sresult.data.dto.SResult<*>.flatMapIfTypeSuspend(
    crossinline block: suspend (I) -> O
): O {
    return if (this::class == I::class) block(this as I)
    else this as O
}

suspend inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>> com.rasalexman.sresult.data.dto.SResult<*>.flatMapIfNotTypeSuspend(
    crossinline block: suspend (com.rasalexman.sresult.data.dto.SResult<*>) -> com.rasalexman.sresult.data.dto.SResult<*>
): com.rasalexman.sresult.data.dto.SResult<*> {
    return if (this !is I) block(this)
    else this
}


suspend inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<*>.flatMapIfDataTypedSuspend(
    crossinline block: suspend (I) -> com.rasalexman.sresult.data.dto.SResult<O>
): com.rasalexman.sresult.data.dto.SResult<O> {
    return if (this.data is I) block(this.data as I)
    else this as com.rasalexman.sresult.data.dto.SResult<O>
}


suspend inline fun <reified I : Any, reified O : Any> ResultList<*>.flatMapIfListDataTypedSuspend(
    crossinline block: suspend (List<I>) -> ResultList<O>
): ResultList<O> {
    return (this.data as? List<I>)?.let { block(it) } ?: this as ResultList<O>
}


suspend inline fun <reified I : Any, reified O : Any> FlowResultList<*>.flatMapIfFlowListDataTypedSuspend(
    crossinline block: suspend (List<I>) -> ResultList<O>
): FlowResultList<O> {
    return this.map {
        it.flatMapIfListDataTypedSuspend(block)
    }
}

suspend inline fun <reified I : com.rasalexman.sresult.data.dto.SResult<*>, reified O : com.rasalexman.sresult.data.dto.SResult<*>> Flow<com.rasalexman.sresult.data.dto.SResult<*>>.flatMapIfFlowTypedSuspend(
    crossinline block: suspend (I) -> O
): Flow<O> {
    return this.map {
        it.flatMapIfTypeSuspend(block)
    }
}

suspend inline fun <reified I : Any, reified O : Any> Flow<com.rasalexman.sresult.data.dto.SResult<*>>.flatMapIfFlowDataTypedSuspend(
    crossinline block: suspend (I) -> com.rasalexman.sresult.data.dto.SResult<O>
): FlowResult<O> {
    return this.map {
        it.flatMapIfDataTypedSuspend(block)
    }
}