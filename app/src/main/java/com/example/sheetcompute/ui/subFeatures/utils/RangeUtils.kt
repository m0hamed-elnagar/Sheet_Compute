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