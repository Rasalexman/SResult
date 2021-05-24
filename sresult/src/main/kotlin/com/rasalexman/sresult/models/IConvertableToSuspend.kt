package com.rasalexman.sresult.models

import kotlin.reflect.KClass

interface IConvertableToSuspend<out T>: IConvertableSuspend {
    suspend fun convertTo(): T

    @Suppress("UNCHECKED_CAST")
    override suspend fun <I : Any> convertAs(clazz: KClass<I>): I? = convertTo() as? I
}

suspend inline fun <reified To : Any> IConvertableSuspend.convert(): To? {
    return convertAs(To::class)
}
