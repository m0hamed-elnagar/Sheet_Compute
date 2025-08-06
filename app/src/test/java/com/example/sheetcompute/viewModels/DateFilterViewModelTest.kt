package com.example.sheetcompute.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asSnapshot
import com.example.sheetcompute.MainDispatcherRule
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.domain.useCases.attendance.GetAttendanceSummaryPagerUseCase
import com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory.DateFilterViewModel
import com.example.sheetcompute.testUtils.MockPagingSource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.InputStream
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class DateFilterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DateFilterViewModel
    private val excelImporter: ExcelImporter = mockk()
    private val preferencesGateway: PreferencesGateway = mockk()
    private val getAttendanceSummaryPagerUseCase: GetAttendanceSummaryPagerUseCase = mockk()

    @Before
    fun setup() {
        every { preferencesGateway.getMonthStartDay() } returns 1


        viewModel = DateFilterViewModel(
            excelImporter,
            preferencesGateway,
            getAttendanceSummaryPagerUseCase
        )
    }

    @Test
    fun `initial state has current year and no month selected`() = runTest {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        assertEquals(currentYear, viewModel.selectedYear.value)
        assertNull(viewModel.selectedMonth.value)
    }

    @Test
    fun `setSelectedYear updates year and triggers refresh`() = runTest {
        viewModel.setSelectedYear(2023)
        assertEquals(2023, viewModel.selectedYear.value)
    }

    @Test
    fun `setSelectedMonth updates month when valid`() = runTest {
        viewModel.setSelectedMonth(5) // June (0-based)
        assertEquals(5, viewModel.selectedMonth.value)
    }

    @Test
    fun `setSelectedMonth ignores invalid month`() = runTest {
        viewModel.setSelectedMonth(13) // Invalid
        assertNull(viewModel.selectedMonth.value)

        viewModel.setSelectedMonth(-1) // Invalid
        assertNull(viewModel.selectedMonth.value)
    }

    @Test
    fun `refreshData increments refreshTrigger`() = runTest {
        val initialValue = viewModel.refreshTrigger.value
        viewModel.refreshData()
        assertEquals(initialValue + 1, viewModel.refreshTrigger.value)
    }

    @Test
    fun `importDataFromExcel calls onError on failure`() = runTest {
        val inputStream: InputStream = mockk(relaxed = true)
        val exception = RuntimeException("Import failed")

        coEvery { excelImporter.import(inputStream) } throws exception

        var errorMessage: String? = null

        viewModel.importDataFromExcel(
            inputStream,
            onComplete = { _, _ -> },
            onError = { msg -> errorMessage = msg }
        )

        advanceUntilIdle()

        assertThat(errorMessage).contains("Failed to import data")
    }

    @Test
    fun `isEmpty is true when PagingData is empty`() = runTest {
        // Given
        val mockPagingSource = MockPagingSource(emptyList())
        val pager = Pager(PagingConfig(20), pagingSourceFactory = { mockPagingSource })

        coEvery {
            getAttendanceSummaryPagerUseCase(any(), any(), any(), any())
        } returns pager


        viewModel.setSelectedMonth(7)
        viewModel.setSelectedYear(2024)
        viewModel.refreshData()
        advanceUntilIdle()
        val snapshot = viewModel.attendanceRecords.asSnapshot()
        assertThat(snapshot).isEmpty()
    }

    @Test
    fun `isEmpty updates when data changes`() = runTest {
        // Create test data
        val fakeRecord = AttendanceRecordUI(
            id = 1,
            name = "John Doe",
            month = 7,
            year = 2024,
            absentCount = 10,
            totalTardyMinutes = 20,
            presentDays = 20
        )

        // Mock the paging source with our test data
        val mockPagingSource = MockPagingSource(listOf(fakeRecord))
        val pager = Pager(PagingConfig(20), pagingSourceFactory = { mockPagingSource })

        coEvery {
            getAttendanceSummaryPagerUseCase(any(), any(), any(), any())
        } returns pager

        viewModel = DateFilterViewModel(
            excelImporter,
            preferencesGateway,
            getAttendanceSummaryPagerUseCase
        )

        viewModel.setSelectedMonth(7)
        viewModel.setSelectedYear(2024)
        viewModel.refreshData()
//        advanceUntilIdle()

        // Get snapshot of paging data
        val snapshot = viewModel.attendanceRecords.asSnapshot()
        assertThat(snapshot).containsExactly(fakeRecord)
    }

}

