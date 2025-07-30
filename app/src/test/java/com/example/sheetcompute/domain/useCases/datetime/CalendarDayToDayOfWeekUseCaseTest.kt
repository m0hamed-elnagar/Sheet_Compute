package com.example.sheetcompute.domain.useCases.datetime

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.util.Calendar

class CalendarDayToDayOfWeekUseCaseTest {
    @Test
    fun `converts calendar days to DayOfWeek`() {
        val input = setOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.FRIDAY)
        val expected = setOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
        val result = CalendarDayToDayOfWeekUseCase.execute(input)
        assertEquals(expected, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws for invalid day`() {
        CalendarDayToDayOfWeekUseCase.execute(setOf(99))
    }
}

