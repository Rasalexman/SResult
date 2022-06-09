@file:Suppress("UNCHECKED_CAST")
package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.data.dto.SResult



inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.getDataIfSuccess(default: I? = null): I? {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Success) this.data
    else default
}


inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapDataIfSuccess(block: (I) -> O): O? {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Success) block(this.data)
    else null
}


inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapDataIfNotSuccess(block: (com.rasalexman.sresult.data.dto.SResult<I>) -> O): O? {
    return if (this !is com.rasalexman.sresult.data.dto.SResult.Success) block(this)
    else null
}


inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapDataIfEmpty(block: () -> O): O? {
    return if (this is com.rasalexman.sresult.data.dto.SResult.Empty) block()
    else null
}


inline fun <reified I : Any, reified O : Any> com.rasalexman.sresult.data.dto.SResult<I>.flatMapDataIfError(block: (com.rasalexman.sresult.data.dto.SResult.AbstractFailure) -> O): O? {
    return if (this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(this)
    else null
}