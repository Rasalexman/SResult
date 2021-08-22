package com.rasalexman.sresult.common.extensions

fun Int?.orZero(): Int {
    return this ?: 0
}

fun Int?.toStringOrEmpty(): String {
    return this?.toString().orEmpty()
}

fun Int.takeIfNotZero(): Int? {
    return this.takeIf { it != 0 }
}