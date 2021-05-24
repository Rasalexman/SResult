package com.rasalexman.sresult.common.extensions

inline fun <T> Iterable<T>.sumByDoubleSafe(selector: (T) -> Double): Double {
    var sum = 0.0
    for (element in this) {
        sum = sum.sumWith(selector(element))
    }
    return sum
}

/**
 * Возвращает или mutableList или пустой mutableList
 * */
fun <T> List<T>?.orEmptyMutable(): MutableList<T> = this?.toMutableList() ?: mutableListOf()
fun <T> Set<T>?.orEmptyMutable(): MutableSet<T> = this?.toMutableSet() ?: mutableSetOf()
fun <T, K> Map<T, K>?.orEmptyMutable(): MutableMap<T, K> = this?.toMutableMap() ?: mutableMapOf()
fun <T, K> List<Pair<T, K>>?.orEmptyMutableMap(): MutableMap<T, K> = this?.toMutableMap() ?: mutableMapOf()

fun <K, V> Iterable<Pair<K, V>>.toMutableMap(): MutableMap<K, V> {
    if (this is Collection) {
        return when (size) {
            0 -> mutableMapOf()
            1 -> mutableMapOf(if (this is List) this[0] else iterator().next())
            else -> toMap(LinkedHashMap<K, V>(size))
        }
    }
    return toMap(LinkedHashMap())
}