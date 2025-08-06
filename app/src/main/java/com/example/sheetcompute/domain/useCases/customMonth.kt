package com.example.sheetcompute.domain.useCases

import java.time.LocalDate

fun createCustomMonthRange(month: Int, year: Int, startDay: Int): ClosedRange<LocalDate>? {
    if (month !in 1..12 && month != 0) return null // safeguard for invalid months
    else {
        return if (month == 0) {
        LocalDate.of(year, 1, 1)..LocalDate.of(year, 12, 31)
    } else {
        val start = LocalDate.of(year, month, 1)
            .withDayOfMonth(startDay)

        val end = start.plusMonths(1).minusDays(1)
        start..end
    }}

}