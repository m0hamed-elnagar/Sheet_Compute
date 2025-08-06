package com.example.sheetcompute.domain.useCases.datetime


import java.time.DayOfWeek
import java.util.Calendar.FRIDAY
import java.util.Calendar.MONDAY
import java.util.Calendar.SATURDAY
import java.util.Calendar.SUNDAY
import java.util.Calendar.THURSDAY
import java.util.Calendar.TUESDAY
import java.util.Calendar.WEDNESDAY

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
