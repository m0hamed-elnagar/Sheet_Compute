package com.example.sheetcompute.domain

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.util.Calendar

class UsecasesKtTest {

    @Test
    fun `given when then`(){
        val weekOfDays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        val expected = setOf(Calendar.SATURDAY, Calendar.SUNDAY) // Saturday and Sunday correspond to 6 and 7 in Calendar
        val result = weekOfDaysToCalendarDays(weekOfDays)
        assertEquals(expected, result)
    }

}