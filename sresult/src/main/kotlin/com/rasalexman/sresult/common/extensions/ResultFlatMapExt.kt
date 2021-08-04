package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.FlowResult
import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.common.typealiases.ResultList
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
        it.flatMapIfSuccess(block)
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
        it.flatMapIfEmpty(block)
    }
}


@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfError(crossinline block: (SResult.AbstractFailure) -> SResult<O>): SResult<O> {
    return if (this is SResult.AbstractFailure) block(this)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowError(crossinline block: (SResult.AbstractFailure) -> SResult<O>): FlowResult<O> {
    return this.map {
        it.flatMapIfError(block)
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
suspend inline fun <reified I : Any, reified O : Any> SResult<*>.flatMapIfSuccessSuspendTyped(
    crossinline block: suspend (I) -> SResult<O>
): SResult<O> {
    return if (this is SResult.Success && data is I) block(this.data as I)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowSuccessSuspend(
    crossinline block: suspend (I) -> SResult<O>
): FlowResult<O> {
    return this.map {
        it.flatMapIfSuccessSuspend(block)
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfErrorSuspend(crossinline block: suspend (SResult.AbstractFailure) -> SResult<O>): SResult<O> {
    return if (this is SResult.AbstractFailure) block(this)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowErrorSuspend(
    crossinline block: suspend (SResult.AbstractFailure) -> SResult<O>
): FlowResult<O> {
    return this.map {
        it.flatMapIfErrorSuspend(block)
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfEmptySuspend(crossinline block: suspend () -> SResult<O>): SResult<O> {
    return if (this is SResult.Empty) block()
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> FlowResult<I>.flatMapIfFlowEmptySuspend(
    crossinline block: suspend () -> SResult<O>
): FlowResult<O> {
    return this.map {
        it.flatMapIfEmptySuspend(block)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : SResult<*>> SResult<*>.flatMapIfType(block: (I) -> SResult<*>): SResult<*> {
    return if (this::class == I::class) block(this as I)
    else this
}


@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<*>.flatMapIfSuccessTyped(
    block: (I) -> SResult<O>
): SResult<O> {
    return if (this is SResult.Success && this.data is I) block(this.data as I)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : SResult<*>, reified O : SResult<*>> SResult<*>.flatMapIfTypeSuspend(
    crossinline block: suspend (I) -> O
): O {
    return if (this::class == I::class) block(this as I)
    else this as O
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<*>.flatMapIfDataTypedSuspend(
    crossinline block: suspend (I) -> SResult<O>
): SResult<O> {
    return if (this.data is I) block(this.data as I)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> ResultList<*>.flatMapIfListDataTypedSuspend(
    crossinline block: suspend (List<I>) -> ResultList<O>
): ResultList<O> {
    return (this.data as? List<I>)?.let { block(it) } ?: this as ResultList<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> FlowResultList<*>.flatMapIfFlowListDataTypedSuspend(
    crossinline block: suspend (List<I>) -> ResultList<O>
): FlowResultList<O> {
    return this.map {
        it.flatMapIfListDataTypedSuspend<I, O>(block)
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : SResult<*>, reified O : SResult<*>> Flow<SResult<*>>.flatMapIfFlowTypedSuspend(
    crossinline block: suspend (I) -> O
): Flow<O> {
    return this.map {
        it.flatMapIfTypeSuspend<I, O>(block)
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> Flow<SResult<*>>.flatMapIfFlowDataTypedSuspend(
    crossinline block: suspend (I) -> SResult<O>
): FlowResult<O> {
    return this.map {
        it.flatMapIfDataTypedSuspend<I, O>(block)
    }
}