package com.example.sheetcompute.ui.subFeatures.utils

import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.format
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

class DateUtilsTest {
    @Test
    fun `formatMinutesToHoursMinutes returns correct format`() {
        assertEquals("2h 5m", DateUtils.formatMinutesToHoursMinutes(125))
        assertEquals("59m", DateUtils.formatMinutesToHoursMinutes(59))
        assertEquals("1h 0m", DateUtils.formatMinutesToHoursMinutes(60))
        assertEquals("0m", DateUtils.formatMinutesToHoursMinutes(0))
    }

    @Test
    fun `formatTimeForStorage returns correct string`() {

        val time = LocalTime.of(6, 29)
        assertEquals("6:29 AM", DateUtils.formatTimeForStorage(time))
        val timePm = LocalTime.of(18, 29)
        assertEquals("6:29 PM", DateUtils.formatTimeForStorage(timePm))
    }

    @Test
    fun `parseTimeString parses various formats`() {
        assertEquals(LocalTime.of(6, 29), DateUtils.parseTimeString("6:29 AM"))
        assertEquals(LocalTime.of(18, 29), DateUtils.parseTimeString("6:29 PM"))
        assertEquals(LocalTime.of(6, 29, 59), DateUtils.parseTimeString("6:29:59AM"))
        assertEquals(LocalTime.of(6, 29, 59), DateUtils.parseTimeString("06:29:59"))
        assertEquals(LocalTime.of(6, 29), DateUtils.parseTimeString("06:29"))
        assertNull(DateUtils.parseTimeString("not a time"))
    }

    @Test
    fun testParseTimeString_invalid() {
        assertNull(DateUtils.parseTimeString("invalid"))
        assertNull(DateUtils.parseTimeString("25:00"))
        assertNull(DateUtils.parseTimeString(""))
        assertNull(DateUtils.parseTimeString("13.45"))
    }

    @Test
    fun `parseDateSafely parses multiple formats`() {
        assertEquals(LocalDate.of(2025, 6, 10), DateUtils.parseDateSafely("10-Jun-2025"))
        assertEquals(LocalDate.of(2025, 6, 10), DateUtils.parseDateSafely("10-06-2025"))
        assertEquals(LocalDate.of(2025, 6, 10), DateUtils.parseDateSafely("2025-06-10"))
        assertEquals(LocalDate.of(2025, 6, 10), DateUtils.parseDateSafely("06/10/2025"))
        assertEquals(LocalDate.of(2025, 6, 4), DateUtils.parseDateSafely("6/4/25"))
        assertEquals(LocalDate.of(2025, 6, 4), DateUtils.parseDateSafely("06/04/25"))
        assertEquals(LocalDate.of(2025, 6, 4), DateUtils.parseDateSafely("6/4/2025"))
        assertNull(DateUtils.parseDateSafely("not a date"))
    }

    @Test
    fun `getMonthName returns correct month or Unknown`() {
        assertEquals("January", DateUtils.getMonthName(1))
        assertEquals("December", DateUtils.getMonthName(12))
        assertEquals("Unknown", DateUtils.getMonthName(13))
    }

    @Test
    fun testGetMonthName_invalid() {
        assertEquals("Unknown", DateUtils.getMonthName(0))
        assertEquals("Unknown", DateUtils.getMonthName(13))
        assertEquals("Unknown", DateUtils.getMonthName(-5))
    }

    @Test
    fun `Date format extension returns correct string`() {
        val date = Date(0) // Epoch
        val formatted = date.format("yyyy-MM-dd")
        assertTrue(formatted.matches(Regex("1970-01-0[1-9]"))) // Accepts any day 1-9 due to timezone
    }


    @Test
    fun `formatDateRange returns correct string`() {
        val d1 = LocalDate.of(2024, 1, 15)
        val d2 = LocalDate.of(2024, 1, 20)
        val d3 = LocalDate.of(2024, 2, 20)
        val d4 = LocalDate.of(2025, 1, 5)
        assertEquals("Jan 15 - 20, 2024", DateUtils.formatDateRange(d1, d2))
        assertEquals("Jan 15 - Feb 20, 2024", DateUtils.formatDateRange(d1, d3))
        assertEquals("Jan 15, 2024 - Jan 5, 2025", DateUtils.formatDateRange(d1, d4))
        assertEquals("Jan 15, 2024", DateUtils.formatDateRange(d1, d1))
        // Same date
        assertEquals("Jan 15, 2024", DateUtils.formatDateRange(d1, d1))

        // Same month
        assertEquals("Jan 15 - 20, 2024", DateUtils.formatDateRange(d1, d2))

        // Same year but different months
        assertEquals("Jan 15 - Feb 20, 2024", DateUtils.formatDateRange(d1, d3))

        // Different years
        assertEquals("Jan 15, 2024 - Jan 5, 2025", DateUtils.formatDateRange(d1, d4))
    }
}


