@file:Suppress("unused", "UselessCallOnCollection")

package com.rasalexman.sresult.common.utils

interface IMapper<in From : Any?, out To : Any?> {
    suspend fun convert(from: From): To
    suspend fun<P : Any> convertWithParams(from: From, params: P): To? = null
}

suspend fun<From : Any, To : Any> IMapper<From, To>.convertList(fromItems: List<From>?): List<To> {
    return fromItems?.mapNotNull { convert(it) }.orEmpty()
}