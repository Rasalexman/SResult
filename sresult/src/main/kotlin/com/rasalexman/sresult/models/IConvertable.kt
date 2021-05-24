package com.rasalexman.sresult.models

import kotlin.reflect.KClass

interface IConvertable {
    fun <I : Any> convertAs(clazz: KClass<I>): I?
}