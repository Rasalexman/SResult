@file:Suppress("unused")
package com.rasalexman.sresultpresentation.extensions

import android.content.res.Resources
import android.os.Bundle
import androidx.core.os.bundleOf

val Int.dp: Float
    get() = this / Resources.getSystem().displayMetrics.density
val Int.px: Float
    get() = this * Resources.getSystem().displayMetrics.density

fun Map<String, Any?>.toBundle(): Bundle = bundleOf(*this.toList().toTypedArray())