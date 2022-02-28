@file:Suppress("unused")

package com.rasalexman.sresult.common.extensions

import android.text.SpannedString
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Iterable<String>.toSQliteSet(): String {
    return this.joinToString(prefix = "(", separator = ",", postfix = ")") { "'$it'" }
}

val randomUUID: String
    get() = UUID.randomUUID().toString()

fun String?.toSpannable(): SpannedString {
    return SpannedString.valueOf(this.orEmpty())
}

fun String?.isSapTrue(): Boolean {
    return this == "X"
}

fun String?.orUUID(): String {
    return this.takeIf { !it.isNullOrEmpty() } ?: randomUUID
}

fun String?.orEmptyValue(default: String? = null): String {
    return this.takeIf { !it.isNullOrEmpty() } ?: default.orEmpty()
}

fun String.isUUID(): Boolean {
    return try {
        UUID.fromString(this)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

fun String.splitByLines(oneLineMaxLength: Int): List<String> {

    val lines = mutableListOf<String>()

    val line = StringBuilder()
    var prefix = ""

    this.split(" ").forEach { word ->
        if ((line.length + word.length + 1) > oneLineMaxLength) {
            lines.add(line.toString())
            line.clear()
            line.append(word)
        } else {
            line.append(prefix)
            line.append(word)
        }
        prefix = " "
    }

    lines.add(line.toString())

    return lines

}

fun String.toDoubleWeight(): Double {
    return takeIf { it.isNotEmpty() }?.toDoubleOrNull() ?: 0.0
}

fun String?.orIfEmpty(input: () -> String): String {
    return if (this == null || this.isEmpty()) input.invoke()
    else this
}

fun String?.toDoubleOrZero(): Double {
    return this?.toDoubleOrNull() ?: 0.0
}

fun String?.toDoubleOrDefault(defaultValue: Double): Double {
    return this?.toDoubleOrNull() ?: defaultValue
}

fun String?.toIntOrZero(): Int {
    return this?.toIntOrNull() ?: 0
}

fun String?.toIntOrDefault(defaultValue: Int): Int {
    return this?.toIntOrNull() ?: defaultValue
}

fun String?.toLongOrZero(): Long {
    return this?.toLongOrNull() ?: 0L
}

fun String?.toLongOrDefault(defaultValue: Long): Long {
    return this?.toLongOrNull() ?: defaultValue
}

fun String?.takeIfNotEmpty() = this?.takeIf { it.isNotEmpty() }

fun String.normalizeSapMessage() = this.lowercase()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

/**
 * Extension method to check if String is Email.
 */
fun String.isEmail(): Boolean {
    val emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$".toRegex(RegexOption.IGNORE_CASE)
    return matches(emailRegex)
}

/**
 * Extension method to check if String is Number.
 */
fun String.isNumeric(): Boolean {
    val p = "^[0-9]+$".toRegex()
    return matches(p)
}

/**
 * Extension method to check String equalsIgnoreCase
 */
fun String.equalsIgnoreCase(other: String) = this.lowercase(Locale.getDefault()).contentEquals(other.lowercase(
    Locale.getDefault()
))

/**
 * Extension method to get Date for String with specified format.
 */
fun String.dateInFormat(format: String): Date? {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    var parsedDate: Date? = null
    try {
        parsedDate = dateFormat.parse(this)
    } catch (ignored: ParseException) {
        ignored.printStackTrace()
    }
    return parsedDate
}