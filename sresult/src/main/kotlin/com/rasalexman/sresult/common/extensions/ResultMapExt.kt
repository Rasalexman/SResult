package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresult.models.IConvertable
import com.rasalexman.sresult.models.IConvertableWithParams
import com.rasalexman.sresult.models.convert
import kotlinx.coroutines.flow.map


// /------- Mapper function
@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any> ResultList<IConvertable>.mapListTo(): ResultList<O> {
    return when (this) {
        is SResult.Success -> {
            data.mapNotNull { it.convert<O>() }.mapToResult()
        }
        else -> this as ResultList<O>
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any, T> ResultList<IConvertableWithParams<O, T>>.mapListToWithParams(params: T): ResultList<O> {
    return when (this) {
        is SResult.Success -> {
            data.mapNotNull { it.convertTo(params) }.mapToResult()
        }
        else -> this as ResultList<O>
    }
}

inline fun <reified O : Any> FlowResultList<IConvertable>.mapFlowListTo(): FlowResultList<O> {
    return this.map {
        it.mapListTo()
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.mapIfSuccess(block: I.() -> SResult<O>): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

inline fun <reified T : List<*>> T.mapToResult(): SResult<T> {
    return this.takeIf { it.isNotEmpty() }.toSuccessResult()
}

inline fun <reified T : Any> T?.mapToResult(): SResult<T> {
    return this.toSuccessResult()
}

inline fun <reified T : Any, O> List<IConvertableWithParams<T, O>>.mapListToWithParams(param: O): List<T> {
    return this.mapNotNull { it.convertTo(param) }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any> SResult<*>.mapIfSuccessTyped(block: I.() -> SResult<*>): SResult<*> {
    return if (this is SResult.Success && this.data is I) block(this.data as I)
    else this
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.mapIfSuccessSuspend(crossinline block: suspend I.() -> SResult<O>): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.mapIfErrorSuspend(crossinline block: suspend (SResult.AbstractFailure) -> SResult<O>): SResult<O> {
    return if (this is SResult.AbstractFailure) block(this)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.mapIfEmptySuspend(crossinline block: suspend () -> SResult<O>): SResult<O> {
    return if (this is SResult.Empty) block()
    else this as SResult<O>
}

inline fun <reified I : SResult<*>> SResult<*>.mapIfType(block: I.() -> SResult<*>): SResult<*> {
    return if (this::class == I::class) block(this as I)
    else this
}

suspend inline fun <reified I : SResult<*>> SResult<*>.mapIfTypeSuspend(crossinline block: suspend I.() -> SResult<*>): SResult<*> {
    return if (this::class == I::class) block(this as I)
    else this
}