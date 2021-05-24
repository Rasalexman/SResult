package com.rasalexman.sresult.common.extensions

import java.util.*

fun Iterable<String>.toSQliteSet(): String {
    return this.joinToString(prefix = "(", separator = ",", postfix = ")") { "'$it'" }
}

fun String?.isSapTrue(): Boolean {
    return this == "X"
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