package com.example.sheetcompute.ui.subFeatures.utils

import java.time.LocalDate


operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    object : Iterator<LocalDate> {
        private var current = start
        override fun hasNext() = current <= endInclusive
        override fun next(): LocalDate {
            if (!hasNext()) throw NoSuchElementException()
            val next = current
            current = current.plusDays(1)
            return next
        }
    }

 fun ClosedRange<LocalDate>.count(predicate: (LocalDate) -> Boolean): Int {
    var count = 0
    for (date in this) {
        if (predicate(date)) {
            count++
        }
    }
    return count
}
fun ClosedRange<LocalDate>.filter(predicate: (LocalDate) -> Boolean): List<LocalDate> {
    val result = mutableListOf<LocalDate>()
    for (date in this) {
        if (predicate(date)) {
            result.add(date)
        }
    }
    return result
}
fun <R> ClosedRange<LocalDate>.map(transform: (LocalDate) -> R): List<R> {
    val result = mutableListOf<R>()
    for (date in this) {
        result.add(transform(date))
    }
    return result
}
fun ClosedRange<LocalDate>.toList(): List<LocalDate> {
    val list = mutableListOf<LocalDate>()
    for (date in this) {
        list.add(date)
    }
    return list
}
