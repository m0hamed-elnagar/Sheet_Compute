package com.example.sheetcompute.domain.useCases.workingDays

import com.example.sheetcompute.data.repo.HolidayRepo
import com.example.sheetcompute.ui.subFeatures.utils.count
import java.time.LocalDate

class CountWorkingDaysUseCase(private val holidayRepo: HolidayRepo) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): Int {
        val nonWorkingDays = GetNonWorkingDaysUseCase(holidayRepo)(start, end)
        return (start..end).count { date -> date !in nonWorkingDays }
    }
}
