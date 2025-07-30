package com.example.sheetcompute.domain.useCases.datetime

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.util.Calendar

class DayOfWeekToCalendarDayUseCaseTest {
    @Test
    fun `converts DayOfWeek set to calendar days`() {
        val input = setOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
        val expected = setOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.FRIDAY)
        val result = DayOfWeekToCalendarDayUseCase.execute(input)
        assertEquals(expected, result)
    }
    //invalid input should return empty set
    @Test
    fun `converts empty DayOfWeek set to empty calendar days`() {
        val input = emptySet<DayOfWeek>()
        val expected = emptySet<Int>()
        val result = DayOfWeekToCalendarDayUseCase.execute(input)
        assertEquals(expected, result)
    }

}

