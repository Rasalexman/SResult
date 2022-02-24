@file:Suppress("UNCHECKED_CAST", "unused")
package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresult.models.IConvertable
import com.rasalexman.sresult.models.IConvertableWithParams
import com.rasalexman.sresult.models.convert
import kotlinx.coroutines.flow.map


// /------- Mapper function

inline fun <reified O : Any> ResultList<IConvertable>.mapListTo(): ResultList<O> {
    return when (this) {
        is SResult.Success -> {
            data.mapNotNull { it.convert<O>() }.mapToResult()
        }
        else -> this as ResultList<O>
    }
}


inline fun <reified O : Any, reified T : Any> ResultList<IConvertableWithParams<O, T>>.mapListToWithParams(params: T): ResultList<O> {
    return when (this) {
        is SResult.Success -> {
            data.mapNotNull { it.convert(params) }.mapToResult()
        }
        else -> this as ResultList<O>
    }
}

inline fun <reified O : Any> FlowResultList<IConvertable>.mapFlowListTo(): FlowResultList<O> {
    return this.map {
        it.mapListTo()
    }
}


inline fun <reified O : Any, reified T : Any> FlowResultList<IConvertableWithParams<O, T>>.mapFlowListToWithParams(params: T): FlowResultList<O> {
    return this.map { result ->
        result.mapListToWithParams(params)
    }
}

inline fun <reified T : List<*>> T.mapToResult(default: SResult<T> = emptyResult()): SResult<T> {
    return this.takeIf { it.isNotEmpty() }.toSuccessResult(default)
}

inline fun <reified T : Any, reified O : Any> List<IConvertableWithParams<T, O>>.mapListToWithParams(param: O): List<T> {
    return this.mapNotNull { it.convert(param) }
}