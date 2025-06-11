package com.example.sheetcompute.domain.useCases.datetime

import java.time.DayOfWeek
import java.util.Calendar.*

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
