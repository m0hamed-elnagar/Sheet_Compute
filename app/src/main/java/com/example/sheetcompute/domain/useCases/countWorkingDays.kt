package com.example.sheetcompute.domain.useCases

import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.domain.repo.HolidayRepo
import com.example.sheetcompute.domain.useCases.datetime.CalendarDayToDayOfWeekUseCase
import com.example.sheetcompute.domain.useCases.datetime.GenerateDateRangeUseCase
import com.example.sheetcompute.ui.subFeatures.utils.count
import com.example.sheetcompute.ui.subFeatures.utils.filter
import java.time.LocalDate

class CalculateWorkingDaysUseCase(
    private val holidayRepo: HolidayRepo
) {
    suspend  fun countWorkingDays(start: LocalDate, end: LocalDate): Int {
        // Weekend days from PreferencesGateway (already in StateFlow as Ints)
        val nonWorking = getNonWorkingDays(start, end)

        return (start..end).count { date ->
            date !in nonWorking
        }
    }
    suspend fun getWorkingDates(start: LocalDate, end: LocalDate): List<LocalDate> {
        val nonWorking = getNonWorkingDays(start, end)
        return (start..end).filter { it !in nonWorking }
    }
    suspend fun getNonWorkingDays(start: LocalDate, end: LocalDate): Set<LocalDate> {
        val weekends = PreferencesGateway.weekendDays.value.let {
            CalendarDayToDayOfWeekUseCase.execute(it)
        }

        val holidayRanges = holidayRepo.getHolidayDatesBetween(start, end)
        val holidays = holidayRanges
            .flatMap { GenerateDateRangeUseCase.execute(it.startDate, it.endDate) }

        val weekendDates = (start..end).filter { it.dayOfWeek in weekends }

        return (holidays + weekendDates).toSet()
    }
}
