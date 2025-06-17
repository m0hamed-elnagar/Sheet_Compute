package com.example.sheetcompute.domain.useCases

import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.domain.repo.HolidayRepo
import com.example.sheetcompute.domain.useCases.datetime.CalendarDayToDayOfWeekUseCase
import com.example.sheetcompute.domain.useCases.datetime.GenerateDateRangeUseCase
import java.time.LocalDate
import java.util.Calendar

operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    object : Iterator<LocalDate> {
        private var current = start
        override fun hasNext() = current <= endInclusive
        override fun next(): LocalDate = current.also {
            current = current.plusDays(1)
        }
    }

class CalculateWorkingDaysUseCase(
    private val holidayRepo: HolidayRepo
) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): Int {
        // Weekend days from PreferencesGateway (already in StateFlow as Ints)
        val weekends = PreferencesGateway.weekendDays.value.let {
            CalendarDayToDayOfWeekUseCase.execute(it)
        }

        // Holidays from DB
        val holidays = holidayRepo.getHolidayDatesBetween(start, end).flatMap { GenerateDateRangeUseCase.execute(start,end) }.toSet()

        return (start..end).count { date ->
            date.dayOfWeek !in weekends && date !in holidays
        }
    }
}

private fun ClosedRange<LocalDate>.count(predicate: (LocalDate) -> Boolean): Int {
    var count = 0
    for (date in this) {
        if (predicate(date)) {
            count++
        }
    }
    return count
}
