package com.example.sheetcompute.domain.useCases

import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.domain.repo.HolidayRepo
import com.example.sheetcompute.domain.useCases.datetime.CalendarDayToDayOfWeekUseCase
import com.example.sheetcompute.domain.useCases.datetime.GenerateDateRangeUseCase
import com.example.sheetcompute.ui.subFeatures.utils.count
import java.time.LocalDate
import java.util.Calendar

class CalculateWorkingDaysUseCase(
    private val holidayRepo: HolidayRepo
) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): Int {
        // Weekend days from PreferencesGateway (already in StateFlow as Ints)
        val weekends = PreferencesGateway.weekendDays.value.let {
            CalendarDayToDayOfWeekUseCase.execute(it)
        }
        //get all holidays in the range
        val holidayRanges = holidayRepo.getHolidayDatesBetween(start, end) // List<HolidayRange>
        val holidays = holidayRanges
            .flatMap { GenerateDateRangeUseCase.execute(it.startDate, it.endDate) }
            .toSet()

        return (start..end).count { date ->
            date.dayOfWeek !in weekends && date !in holidays
        }
    }
}

