package com.rasalexman.sresult.models

import kotlin.reflect.KClass

interface IConvertableWithParams<out T, in R : Any?> : IConvertable {
    fun convertTo(param: R): T?

    override fun <I : Any> convertAs(clazz: KClass<I>): I? {
        return null
    }
}