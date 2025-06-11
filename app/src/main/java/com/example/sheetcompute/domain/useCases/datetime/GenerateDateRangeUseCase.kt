package com.example.sheetcompute.domain.useCases.datetime

import java.time.LocalDate

object GenerateDateRangeUseCase {
    fun execute(start: LocalDate, end: LocalDate): List<LocalDate> {
        val result = mutableListOf<LocalDate>()
        var current = start
        while (!current.isAfter(end)) {
            result.add(current)
            current = current.plusDays(1)
        }
        return result
    }
}
