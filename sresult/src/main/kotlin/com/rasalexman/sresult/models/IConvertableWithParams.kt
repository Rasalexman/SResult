package com.rasalexman.sresult.models

import kotlin.reflect.KClass

interface IConvertableWithParams<out T, in R : Any?> : IConvertable {
    fun convertTo(param: R): T?

    @Suppress("UNCHECKED_CAST")
    fun <I : Any> convertAsWithParams(clazz: KClass<I>, params: R): I? {
        return convertTo(params) as? I
    }

    override fun <I : Any> convertAs(clazz: KClass<I>): I? = null
}

inline fun <reified To : Any, O: Any> IConvertableWithParams<To, O>.convert(params: O): To? {
    return convertAsWithParams(To::class, params)
}