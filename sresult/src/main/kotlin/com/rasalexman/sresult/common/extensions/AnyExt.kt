package com.rasalexman.sresult.common.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


fun <T> Any?.implementationOf(clazz: Class<T>): T? {
    return if (this != null && clazz.isInstance(this)) {
        @Suppress("UNCHECKED_CAST")
        this as T
    } else {
        return null
    }

}

fun<T : Any> List<T>?.listSize(): Int {
    return this?.size.orZero()
}

/**
 * Do action if caller is null or return caller
 */
fun <T> T?.or(handler: () -> T): T {
    return this ?: handler.invoke()
}

/**
 * Suspend or
 */
suspend inline fun <T> T?.sor(noinline handler: suspend () -> T): T {
    return this ?: handler.invoke()
}

suspend fun <T> doAsync(block: suspend () -> T): T = withContext(Dispatchers.IO) {
    block()
}

suspend fun <T> doOnDefault(block: suspend () -> T): T = withContext(Dispatchers.Default) {
    block()
}

fun tryOrEmpty(tryFunc: () -> String): String {
    return tryOrDefault("") { tryFunc() }
}

fun tryOrNow(tryFunc: () -> Date?): Date {
    return tryOrDefault(Date()) { tryFunc() }
}

fun <T : Any> tryOrNull(tryFunc: () -> T): T? {
    return try {
        tryFunc()
    } catch (e: Exception) {
        Timber.e("tryOrNull exception: ${e.message}")
        null
    }
}

fun <T : Any> tryOrDefault(defaultIfCatches: T, tryFunc: () -> T?): T {
    return try {
        tryFunc() ?: defaultIfCatches
    } catch (e: Exception) {
        Timber.e("tryOrDefault exception: ${e.message}")
        defaultIfCatches
    }
}