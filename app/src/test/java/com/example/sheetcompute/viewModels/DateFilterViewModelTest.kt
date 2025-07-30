package com.example.sheetcompute.viewModels

import androidx.paging.Pager
import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.sheetcompute.MainDispatcherRule
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.domain.useCases.attendance.GetAttendanceSummaryPagerUseCase
import com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory.DateFilterViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.InputStream
//todo
@OptIn(ExperimentalCoroutinesApi::class)
class DateFilterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Your custom rule if needed

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
    fun `setting valid year and month creates pager`() = runTest {
        val fakePagingData = PagingData.from(listOf<AttendanceRecordUI>())
        val fakePager = mockk<Pager<Int, AttendanceRecordUI>>() {
            every { flow } returns flowOf(fakePagingData)
        }
        coEvery {
            getAttendanceSummaryPagerUseCase(any(), any(), any(), any())
        } returns fakePager

        viewModel.setSelectedYear(2024)
        viewModel.setSelectedMonth(6)

        viewModel.attendanceRecords.test {
            val item = awaitItem()
            assertThat(item).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshData triggers paging reload`() = runTest {
        val data1 = PagingData.from(listOf<AttendanceRecordUI>())
        val data2 = PagingData.from(listOf<AttendanceRecordUI>())
        var callCount = 0
        coEvery {
            getAttendanceSummaryPagerUseCase(any(), any(), any(), any())
        } answers {
            callCount++
            val fakePager = mockk<Pager<Int, AttendanceRecordUI>>() {
                every { flow } returns flowOf(if (callCount == 1) data1 else data2)
            }
            fakePager
        }

        viewModel.setSelectedYear(2024)
        viewModel.setSelectedMonth(5)

        viewModel.attendanceRecords.test {
            awaitItem()
            viewModel.refreshData()
            awaitItem()
            assertThat(callCount).isEqualTo(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isEmpty updates when empty paging emitted`() = runTest {
        val fakePager = mockk<Pager<Int, AttendanceRecordUI>>() {
            every { flow } returns flowOf(PagingData.empty())
        }
        coEvery {
            getAttendanceSummaryPagerUseCase(any(), any(), any(), any())
        } returns fakePager

        viewModel.setSelectedYear(2024)
        viewModel.setSelectedMonth(3)

        viewModel.isEmpty.test {
            assertThat(awaitItem()).isFalse() // initial
            assertThat(awaitItem()).isTrue()  // after paging empty
            cancelAndIgnoreRemainingEvents()
        }
    }

//    @Test
//    fun `importDataFromExcel calls onComplete and refreshes`() = runTest {
//        val result = ExcelImporter.ImportResult(recordsAdded = 5, newEmployees = 2, errors = emptyList(),
//            duplicates = emptyList())
//        val inputStream: InputStream = mockk(relaxed = true)
//
//        coEvery { excelImporter.import(inputStream) } returns result
//
//        var completedMessage: String? = null
//        var wasRefreshed = false
//
//        viewModel = object : DateFilterViewModel(
//            excelImporter,
//            preferencesGateway,
//            getAttendanceSummaryPagerUseCase
//        ) {
//            override fun refreshData() {
//                wasRefreshed = true
//            }
//        }
//
//        viewModel.importDataFromExcel(
//            inputStream,
//            onComplete = { msg, res -> completedMessage = msg },
//            onError = {}
//        )
//
//        advanceUntilIdle()
//
//        assertThat(completedMessage).contains("Imported")
//        assertThat(wasRefreshed).isTrue()
//    }

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
}
