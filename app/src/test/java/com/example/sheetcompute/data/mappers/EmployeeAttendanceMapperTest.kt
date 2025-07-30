package com.example.sheetcompute.data.mappers

import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class EmployeeAttendanceMapperTest {
    @Test
    fun `maps to PRESENT when not holiday and not late`() {
        val record = AttendanceRecord(
            id = 1L,
            employeeId = 2L,
            date = LocalDate.of(2024, 7, 1),
            clockIn = "08:00",
            tardyMinutes = 0
        )
        val result = record.toEmployeeAttendanceRecord(emptyList())
        assertEquals(AttendanceStatus.PRESENT, result.status)
        assertEquals(0, result.lateDuration)
    }

    @Test
    fun `maps to LATE when tardyMinutes greater than zero`() {
        val record = AttendanceRecord(
            id = 2L,
            employeeId = 2L,
            date = LocalDate.of(2024, 7, 2),
            clockIn = "08:10",
            tardyMinutes = 10
        )
        val result = record.toEmployeeAttendanceRecord(emptyList())
        assertEquals(AttendanceStatus.LATE, result.status)
        assertEquals(10, result.lateDuration)
    }

    @Test
    fun `maps to EXTRA_DAY when date is in holidays`() {
        val record = AttendanceRecord(
            id = 3L,
            employeeId = 2L,
            date = LocalDate.of(2024, 7, 3),
            clockIn = "08:00",
            tardyMinutes = 0
        )
        val result = record.toEmployeeAttendanceRecord(listOf(LocalDate.of(2024, 7, 3)))
        assertEquals(AttendanceStatus.EXTRA_DAY, result.status)
    }
}

