package com.rasalexman.sresult.common.extensions

fun Float?.orZero(): Float {
    return this ?: 0f
}

fun Float?.toStringOrEmpty(): String {
    return this?.toString().orEmpty()
}

fun Float?.takeIfNotZero(): Float? {
    return this.takeIf { it != null && it != 0f }
}