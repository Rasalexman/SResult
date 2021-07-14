package com.rasalexman.sresult.models

import kotlin.reflect.KClass

interface IConvertableSuspend : IConvertable {
    suspend fun <I : Any> convertAsSuspend(clazz: KClass<I>): I?
}