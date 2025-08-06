package com.example.sheetcompute.domain.useCases.attendance

import androidx.paging.Pager
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import org.junit.Assert.*

import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.domain.useCases.workingDays.CountWorkingDaysUseCase
import com.example.sheetcompute.domain.useCases.workingDays.GetNonWorkingDaysUseCase
import com.example.sheetcompute.domain.usecase.GetEmployeeAttendanceRecordsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.collections.get
import kotlin.invoke

class GetEmployeeAttendanceRecordsUseCaseTest {

    private lateinit var attendanceRepo: AttendanceRepo
    private lateinit var getNonWorkingDaysUseCase: GetNonWorkingDaysUseCase
    private lateinit var useCase: GetEmployeeAttendanceRecordsUseCase

    @Before
    fun setup() {
        attendanceRepo = mockk()
        getNonWorkingDaysUseCase = mockk()
        useCase = GetEmployeeAttendanceRecordsUseCase(attendanceRepo, getNonWorkingDaysUseCase)
    }

    @Test
    fun `returns full date range with absent for missing records`() = runTest {
        // Given
        val employeeId = 1L
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 3)

        val nonWorkingDays = setOf(LocalDate.of(2024, 7, 2)) // Only 2nd is a holiday
        coEvery { getNonWorkingDaysUseCase(start, end) } returns nonWorkingDays


        coEvery {
            attendanceRepo.getEmployeeAttendanceRecordsByRange(employeeId, start, end)
        } returns listOf(
            AttendanceRecord(1L, employeeId, LocalDate.of(2024, 7, 1), "08:00 AM", 0),
            AttendanceRecord(2L, employeeId, LocalDate.of(2024, 7, 2), "09:00 AM", 0)
        )

        // Act
        val result = useCase(employeeId, start, end)

        // Assert
        assertEquals(3, result.size)

        assertEquals(LocalDate.of(2024, 7, 1), result[0].date)
        assertEquals(AttendanceStatus.PRESENT, result[0].status)

        assertEquals(LocalDate.of(2024, 7, 2), result[1].date)
        assertEquals(AttendanceStatus.EXTRA_DAY, result[1].status)

        assertEquals(LocalDate.of(2024, 7, 3), result[2].date)
        assertEquals(AttendanceStatus.ABSENT, result[2].status)
    }
}