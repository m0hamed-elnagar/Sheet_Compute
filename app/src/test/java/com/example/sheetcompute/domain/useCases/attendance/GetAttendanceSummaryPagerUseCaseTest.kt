package com.example.sheetcompute.domain.useCases.attendance

import androidx.paging.Pager
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import org.junit.Assert.*

import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.domain.useCases.workingDays.CountWorkingDaysUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetAttendanceSummaryPagerUseCaseTest {

    val repo: AttendanceRepo = mockk(relaxed = true)
    private val workingDaysUseCase: CountWorkingDaysUseCase = mockk()
    private lateinit var useCase: GetAttendanceSummaryPagerUseCase

    @Before
    fun setup() {
        useCase = GetAttendanceSummaryPagerUseCase(repo, workingDaysUseCase)
    }

    @Test
    fun `invoke should return pager with calculated working days`() = runTest {
        // Given
        val range = LocalDate.of(2024, 7, 1)..LocalDate.of(2024, 7, 31)
        val expectedWorkingDays = 20
        val expectedPager: Pager<Int, AttendanceRecordUI> = mockk()

        coEvery { workingDaysUseCase(range.start, range.endInclusive) } returns expectedWorkingDays
        coEvery {
            repo.getPagedAttendanceSummaries(
                month = 7,
                year = 2024,
                range = range,
                totalWorkingDays = expectedWorkingDays,
                pageSize = 10
            )
        } returns expectedPager

    // When
    val result = useCase.invoke(
        month = 7,
        year = 2024,
        range = range,
        pageSize = 10
    )

    // Then
    assertEquals(expectedPager, result)
}}