package com.example.sheetcompute.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar

fun weekOfDaysToCalendarDays(weekOfDays: Set<DayOfWeek>): Set<Int> =
    weekOfDays.map {
        when (it) {
            DayOfWeek.MONDAY -> Calendar.MONDAY
            DayOfWeek.TUESDAY -> Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> Calendar.THURSDAY
            DayOfWeek.FRIDAY -> Calendar.FRIDAY
            DayOfWeek.SATURDAY -> Calendar.SATURDAY
            DayOfWeek.SUNDAY -> Calendar.SUNDAY
        }
    }.toSet()

fun rangeToLocalDateRange(
    start: java.time.LocalDate,
    end: java.time.LocalDate
): MutableList<LocalDate> {
    val days = mutableListOf<LocalDate>()
    var current = start
    while (!current.isAfter(end)) {
        days.add(current)
        current = current.plusDays(1)
    }
    return days

}