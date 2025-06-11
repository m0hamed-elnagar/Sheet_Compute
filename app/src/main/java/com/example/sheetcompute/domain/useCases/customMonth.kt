package com.example.sheetcompute.domain.useCases

import com.example.sheetcompute.domain.PreferencesGateway
import java.time.LocalDate

 fun createCustomMonthRange(month: Int, year: Int): ClosedRange<LocalDate>? {
    val startDay = PreferencesGateway.getMonthStartDay()
    val endDay = if (startDay == 1) LocalDate.of(year, month, 1).lengthOfMonth() else startDay - 1
    return if (month == 0) {
        // Special case: all months selected
        val start = LocalDate.of(year - 1, 12, startDay)
        val end = LocalDate.of(year, 12, endDay)
        start..end
    } else {
        val start: LocalDate
        val end: LocalDate

        if (month == 1) {
            // January: previous year December startDay to current year January endDay
            start = LocalDate.of(year - 1, 12, startDay)
            end = LocalDate.of(year, 1, endDay)
        } else {
            // All other months
            start = LocalDate.of(year, month - 1, startDay)
            end = LocalDate.of(year, month, endDay)
        }
        start..end
    }
}
