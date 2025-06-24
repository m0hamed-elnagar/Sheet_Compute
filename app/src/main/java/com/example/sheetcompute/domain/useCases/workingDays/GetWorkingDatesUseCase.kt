package com.example.sheetcompute.domain.useCases.workingDays

import com.example.sheetcompute.domain.repo.HolidayRepo
import com.example.sheetcompute.ui.subFeatures.utils.filter
import java.time.LocalDate

class GetWorkingDatesUseCase(private val holidayRepo: HolidayRepo) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): List<LocalDate> {
        val nonWorkingDays = GetNonWorkingDaysUseCase(holidayRepo)(start, end)
        return (start..end).filter { it !in nonWorkingDays }
    }
}
