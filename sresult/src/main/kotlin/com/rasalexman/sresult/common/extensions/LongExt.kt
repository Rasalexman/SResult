package com.rasalexman.sresult.common.extensions

fun Long?.orZero(): Long {
    return this ?: 0L
}

fun Long?.toStringOrEmpty(): String {
    return this?.toString().orEmpty()
}

fun Long?.takeIfNotZero(): Long? {
    return this.takeIf { it != null && it != 0L }
}