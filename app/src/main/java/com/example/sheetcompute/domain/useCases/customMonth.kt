package com.example.sheetcompute.domain.useCases

import java.time.LocalDate

 fun createCustomMonthRange(month: Int, year: Int, startDay: Int): ClosedRange<LocalDate>? {
     if (month !in 1..12 && month != 0) return null // safeguard for invalid months
     val endDay = if (startDay == 1)
         LocalDate.of(year, if (month == 0) 12 else month, 1).lengthOfMonth()
     else
         startDay - 1
     return if (month == 0) {
        // Special case: all months selected
        val start = LocalDate.of(year - 1, 12, startDay)
        val end = LocalDate.of(year, 12, endDay)
        start..end
    } else {
        val start = if (month == 1)
            LocalDate.of(year - 1, 12, startDay)
        else
            LocalDate.of(year, month - 1, startDay)

        val end = LocalDate.of(year, month, endDay)
        start..end
    }
}
