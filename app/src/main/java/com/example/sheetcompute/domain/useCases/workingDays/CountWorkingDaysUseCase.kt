package com.example.sheetcompute.domain.useCases.workingDays

import com.example.sheetcompute.ui.subFeatures.utils.count
import java.time.LocalDate
import javax.inject.Inject

class CountWorkingDaysUseCase @Inject constructor(private val getNonWorkingDaysUseCase: GetNonWorkingDaysUseCase) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): Int {
        val nonWorkingDays = getNonWorkingDaysUseCase(start, end)
        return (start..end).count { date -> date !in nonWorkingDays }
    }
}
