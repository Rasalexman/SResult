package com.rasalexman.sresult.common.extensions

import android.content.res.Resources

val Int.dp: Float
    get() = this / Resources.getSystem().displayMetrics.density
val Int.px: Float
    get() = this * Resources.getSystem().displayMetrics.density

fun Int?.orZero(): Int {
    return this ?: 0
}

fun Int?.toStringOrEmpty(): String {
    return this?.toString().orEmpty()
}

fun Int.takeIfNotZero(): Int? {
    return this.takeIf { it != 0 }
}