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

/**
 * Filtered first list by list predicate. Example:
 *
 * val list1 = listOf(User(1, "Alice"), User(2, "Mikhail"), User(3, "Sasha"))
 * val list2 = listOf(User(5, "Alice"), User(3, "Mikhail"))
 *
 * val result = list1.subtract(list2) { a, b ->
 *     a.name != b.name
 * }
 * result == listOf(User(3, "Sasha"))
 *
 * list1.removeAll(list2) not suitable because data classes may differ in other fields,
 * and we need to compare only for certain fields
 * */
fun <T, U> List<T>.subtract(
    secondList: Collection<U>,
    filterPredicate: (T, U) -> Boolean,
): List<T> {
    return if (secondList.isNotEmpty()) {
        this.filter { item -> secondList.all { filterPredicate(item, it) } }
    } else this.toList()
}