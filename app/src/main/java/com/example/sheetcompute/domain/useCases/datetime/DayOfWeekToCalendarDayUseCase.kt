package com.example.sheetcompute.domain.useCases.datetime

import java.time.DayOfWeek
import java.util.Calendar.FRIDAY
import java.util.Calendar.MONDAY
import java.util.Calendar.SATURDAY
import java.util.Calendar.SUNDAY
import java.util.Calendar.THURSDAY
import java.util.Calendar.TUESDAY
import java.util.Calendar.WEDNESDAY

object DayOfWeekToCalendarDayUseCase {
    fun execute(days: Set<DayOfWeek>): Set<Int> {
        return days.map {
            when (it) {
                DayOfWeek.SUNDAY -> SUNDAY
                DayOfWeek.MONDAY -> MONDAY
                DayOfWeek.TUESDAY -> TUESDAY
                DayOfWeek.WEDNESDAY -> WEDNESDAY
                DayOfWeek.THURSDAY -> THURSDAY
                DayOfWeek.FRIDAY -> FRIDAY
                DayOfWeek.SATURDAY -> SATURDAY
            }
        }.toSet()
    }
}
