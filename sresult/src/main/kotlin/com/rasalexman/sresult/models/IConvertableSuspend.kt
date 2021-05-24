package com.rasalexman.sresult.models

import kotlin.reflect.KClass

interface IConvertableSuspend {
    suspend fun <I : Any> convertAs(clazz: KClass<I>): I?
}