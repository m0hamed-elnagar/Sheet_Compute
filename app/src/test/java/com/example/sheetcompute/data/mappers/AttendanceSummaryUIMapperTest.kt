package com.example.sheetcompute.data.mappers

import com.example.sheetcompute.data.entities.AttendanceSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class AttendanceSummaryUIMapperTest {
    @Test
    fun `maps AttendanceSummary to AttendanceRecordUI correctly`() {
        val summary = AttendanceSummary(
            id = 1L,
            name = "John Doe",
            month = 7,
            year = 2024,
            presentDays = 18,
            totalTardyMinutes = 30
        )
        val totalWorkingDays = 22
        val ui = summary.mapToAttendanceRecordUI(totalWorkingDays)
        assertEquals(1L, ui.id)
        assertEquals("John Doe", ui.name)
        assertEquals(7, ui.month)
        assertEquals(2024, ui.year)
        assertEquals(4, ui.absentCount)
        assertEquals(30, ui.totalTardyMinutes)
        assertEquals(18, ui.presentDays)
    }
}

