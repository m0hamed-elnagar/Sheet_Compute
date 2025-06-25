package com.example.sheetcompute.domain.useCases.workingDays

import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.data.repo.HolidayRepo
import com.example.sheetcompute.domain.useCases.datetime.CalendarDayToDayOfWeekUseCase
import com.example.sheetcompute.domain.useCases.datetime.GenerateDateRangeUseCase
import com.example.sheetcompute.ui.subFeatures.utils.filter
import java.time.LocalDate

class GetNonWorkingDaysUseCase(private val holidayRepo: HolidayRepo) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): Set<LocalDate> {
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
