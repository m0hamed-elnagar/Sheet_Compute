package com.example.sheetcompute.domain.useCases

import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar.*

 fun calendarDaysToDayOfWeekSet(weekendDays: Set<Int>): Set<DayOfWeek> {
    return weekendDays.map { dayOfWeek ->
        when (dayOfWeek) {
            SUNDAY -> DayOfWeek.SUNDAY
            MONDAY -> DayOfWeek.MONDAY
            TUESDAY -> DayOfWeek.TUESDAY
            WEDNESDAY -> DayOfWeek.WEDNESDAY
            THURSDAY -> DayOfWeek.THURSDAY
            FRIDAY -> DayOfWeek.FRIDAY
            SATURDAY -> DayOfWeek.SATURDAY
            else -> throw IllegalArgumentException("Invalid day of week: $dayOfWeek")
        }
    }.toSet()
}
fun Set<DayOfWeek>.calenderToDayOfWeek(): Set<Int> = map { dayOfWeek ->
    when (dayOfWeek) {
        DayOfWeek.SUNDAY -> SUNDAY
        DayOfWeek.MONDAY -> MONDAY
        DayOfWeek.TUESDAY -> TUESDAY
        DayOfWeek.WEDNESDAY -> WEDNESDAY
        DayOfWeek.THURSDAY -> THURSDAY
        DayOfWeek.FRIDAY -> FRIDAY
        DayOfWeek.SATURDAY -> SATURDAY
    }
}.toSet()
fun rangeToDays(
    start: LocalDate,
    end: LocalDate
): MutableList<LocalDate> {
    val days = mutableListOf<LocalDate>()
    var current = start
    while (!current.isAfter(end)) {
        days.add(current)
        current = current.plusDays(1)
    }
    return days

}