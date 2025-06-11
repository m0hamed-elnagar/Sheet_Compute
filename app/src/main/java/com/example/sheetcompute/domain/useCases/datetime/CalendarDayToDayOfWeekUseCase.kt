package com.example.sheetcompute.domain.useCases.datetime


import java.time.DayOfWeek
import java.util.Calendar.*

object CalendarDayToDayOfWeekUseCase {
    fun execute(weekendDays: Set<Int>): Set<DayOfWeek> {
        return weekendDays.map {
            when (it) {
                SUNDAY -> DayOfWeek.SUNDAY
                MONDAY -> DayOfWeek.MONDAY
                TUESDAY -> DayOfWeek.TUESDAY
                WEDNESDAY -> DayOfWeek.WEDNESDAY
                THURSDAY -> DayOfWeek.THURSDAY
                FRIDAY -> DayOfWeek.FRIDAY
                SATURDAY -> DayOfWeek.SATURDAY
                else -> throw IllegalArgumentException("Invalid day: $it")
            }
        }.toSet()
    }
}
