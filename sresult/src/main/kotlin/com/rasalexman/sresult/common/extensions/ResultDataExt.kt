package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.data.dto.SResult


@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any> SResult<I>.collectDataIfSuccess(default: I? = null): I? {
    return if (this is SResult.Success) this.data
    else default
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapDataIfSuccess(block: (I) -> O): O? {
    return if (this is SResult.Success) block(this.data)
    else null
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapDataIfNotSuccess(block: (SResult<I>) -> O): O? {
    return if (this !is SResult.Success) block(this)
    else null
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapDataIfEmpty(block: () -> O): O? {
    return if (this is SResult.Empty) block()
    else null
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapDataIfError(block: (SResult.AbstractFailure) -> O): O? {
    return if (this is SResult.AbstractFailure) block(this)
    else null
}