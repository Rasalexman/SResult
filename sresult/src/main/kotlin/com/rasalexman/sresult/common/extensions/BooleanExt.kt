package com.rasalexman.sresult.common.extensions

fun Boolean?.orTrue(): Boolean = this ?: true
fun Boolean?.orFalse(): Boolean = this ?: false
fun Boolean.toInt() = if (this) 1 else 0