package com.example.sheetcompute.domain.useCases.workingDays

import com.example.sheetcompute.ui.subFeatures.utils.filter
import java.time.LocalDate
import javax.inject.Inject

class GetWorkingDatesUseCase @Inject constructor(private val getNonWorkingDaysUseCase: GetNonWorkingDaysUseCase
) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): List<LocalDate> {
        val nonWorkingDays = getNonWorkingDaysUseCase(start, end)
        return (start..end).filter { it !in nonWorkingDays }
    }
}
