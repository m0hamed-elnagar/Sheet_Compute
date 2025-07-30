package com.example.sheetcompute.domain.useCases.datetime

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GenerateDateRangeUseCaseTest {
    @Test
    fun `generates correct range for same start and end`() {
        val date = LocalDate.of(2024, 7, 1)
        val result = GenerateDateRangeUseCase.execute(date, date)
        assertEquals(listOf(date), result)
    }

    @Test
    fun `generates correct range for multiple days`() {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 3)
        val result = GenerateDateRangeUseCase.execute(start, end)
        assertEquals(
            listOf(
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 2),
                LocalDate.of(2024, 7, 3)
            ),
            result
        )
    }
}

