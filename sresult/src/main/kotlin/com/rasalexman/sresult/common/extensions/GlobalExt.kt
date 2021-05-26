package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.BuildConfig
import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.common.typealiases.OutHandler
import timber.log.Timber

const val DEFAULT_TAG = "------> "

inline fun Any.logg(lambda: () -> String?) {
    //if (BuildConfig.DEBUG) {
        Timber.d("$DEFAULT_TAG${this::class.java} ${lambda().orEmpty()}")
    //}
}

fun Any.logg(message: String?, tag: String = DEFAULT_TAG) {
    //if (BuildConfig.DEBUG) {
        Timber.d("$tag ${this::class.java} ${message.orEmpty()}")
   // }
}

fun Any.loggE(exception: Throwable? = null, message: String? = null) {
    if(this is Throwable) {
        Timber.e(this, message)
    } else {
        Timber.e(exception, message)
    }
}

fun Any.loggE(lambda: () -> String) {
    Timber.e(this.toString(), lambda())
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

inline fun <reified T, reified R> R.unsafeLazy(noinline init: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, init)

inline fun runIfDebug(function: () -> Unit) {
    if (BuildConfig.DEBUG) {
        function()
    }
}

inline fun runIfRelease(function: () -> Unit) {
    if (!BuildConfig.DEBUG) {
        function()
    }
}

fun <T: Any>T?.logIfNull(message: String): T? {
    if (this == null) {
        Timber.e(message)
    }
    return this
}
