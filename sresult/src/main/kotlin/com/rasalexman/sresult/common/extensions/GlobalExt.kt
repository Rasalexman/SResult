@file:Suppress("unused")
package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.common.typealiases.OutHandler
import timber.log.Timber

const val DEFAULT_TAG = "------>"

inline fun Any.logg(lambda: () -> String?) {
    Timber.d("$DEFAULT_TAG [${this::class.simpleName}]: ${lambda().orEmpty()}")
}

fun Any.logg(message: String?, tag: String = DEFAULT_TAG) {
    val realMessage = message.orEmpty()
    val output = if(this is String) {
        "$tag [$this]: $realMessage"
    } else if(this is Throwable) {
        "$tag [${this.toString()}]: $realMessage"
    } else {
        "$tag [${this::class.simpleName}]: $realMessage"
    }
    Timber.d(output)
}

fun Any.loggE(exception: Throwable? = null, message: String? = null) {
    if(this is Throwable) {
        Timber.e("$DEFAULT_TAG [${this.toString()}]: ${message.orEmpty()}")
    } else {
        Timber.e(exception, message)
    }
}

fun Any.loggE(lambda: () -> String) {
    val message = if(this is String) {
        "$DEFAULT_TAG [$this]: ${lambda()}"
    } else if(this is Throwable) {
        "$DEFAULT_TAG [${this.toString()}]: ${lambda()}"
    } else {
        "$DEFAULT_TAG [${this::class.simpleName}]: ${lambda()}"
    }
    Timber.e(message)
}

fun <T, R> T?.doIfNull(input: OutHandler<R>): Any? {
    return this ?: input()
}

inline fun <reified T> T?.orIfNull(input: () -> T): T {
    return this ?: input()
}

fun <T> T.applyIf(prediction: Boolean, input: InHandler<T>): T {
    if (prediction) input.invoke(this)
    return this
}

inline fun <reified I : Any> Any.applyForType(block: (I) -> Unit): Any {
    if (this is I) block(this)
    return this
}

inline fun <reified I : Any, reified R : Any> Any.flatMapForType(block: (I) -> R): Any {
    return if (this is I) block(this)
    else this
}

inline fun <reified T, reified R> R.unsafeLazy(noinline init: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, init)


fun <T: Any>T?.logIfNull(message: String): T? {
    if (this == null) {
        Timber.e(message)
    }
    return this
}
