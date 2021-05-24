package com.rasalexman.sresult.models

import kotlin.reflect.KClass

interface IConvertableTo<out T> : IConvertable {
    fun convertTo(): T?

    @Suppress("UNCHECKED_CAST")
    override fun <I : Any> convertAs(clazz: KClass<I>): I? = convertTo() as? I
}

inline fun <reified To : Any> IConvertable.convert(): To? {
    return convertAs(To::class)
}

