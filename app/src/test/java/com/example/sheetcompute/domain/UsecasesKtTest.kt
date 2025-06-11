package com.example.sheetcompute.domain

import com.example.sheetcompute.domain.useCases.datetime.*
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.util.Calendar

class UsecasesKtTest {

    @Test
    fun `given when then`(){
        val expected = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        val calendarDays = setOf(Calendar.SATURDAY, Calendar.SUNDAY) // Saturday and Sunday correspond to 6 and 7 in Calendar
        val result = CalendarDayToDayOfWeekUseCase.execute(calendarDays)
        assertEquals(expected, result)
    }

}