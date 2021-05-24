package com.rasalexman.sresult.common.extensions

import java.util.*

operator fun Date.plus(other: Date): Date = Date(this.time + other.time)
operator fun Date.minus(other: Date): Date = Date(this.time - other.time)

fun Date?.orToday() = this ?: Date()