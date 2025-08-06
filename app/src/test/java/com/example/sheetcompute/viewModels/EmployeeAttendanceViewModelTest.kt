package com.example.sheetcompute.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.sheetcompute.MainDispatcherRule
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.EmployeeRepo
import com.example.sheetcompute.domain.usecase.GetEmployeeAttendanceRecordsUseCase
import com.example.sheetcompute.ui.features.employeeAttendance.EmployeeAttendanceViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class EmployeeAttendanceViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var preferencesGateway: PreferencesGateway
    private lateinit var getEmployeeAttendanceRecordsUseCase: GetEmployeeAttendanceRecordsUseCase
    private lateinit var employeeRepo: EmployeeRepo
    private lateinit var viewModel: EmployeeAttendanceViewModel

    private val presentCountObserver = Observer<Int> {}
    private val absentCountObserver = Observer<Int> {}
    private val extraDaysObserver = Observer<Int> {}
    private val tardiesObserver = Observer<Long> {}
    private val isEmptyObserver = Observer<Boolean> {}

    @Before
    fun setUp() {
        preferencesGateway = mockk(relaxed = true)
        employeeRepo = mockk()
        getEmployeeAttendanceRecordsUseCase = mockk()
        every { preferencesGateway.getMonthStartDay() } returns 1     // 1st of month

        viewModel = EmployeeAttendanceViewModel(
            getEmployeeAttendanceRecordsUseCase,
            employeeRepo,
            preferencesGateway
        )

        // Always observe LiveData so .value is available
        viewModel.presentCount.observeForever(presentCountObserver)
        viewModel.absentCount.observeForever(absentCountObserver)
        viewModel.extraDaysCount.observeForever(extraDaysObserver)
        viewModel.tardiesCount.observeForever(tardiesObserver)
        viewModel.isEmpty.observeForever(isEmptyObserver)
    }

    @After
    fun tearDown() {
        viewModel.presentCount.removeObserver(presentCountObserver)
        viewModel.absentCount.removeObserver(absentCountObserver)
        viewModel.extraDaysCount.removeObserver(extraDaysObserver)
        viewModel.tardiesCount.removeObserver(tardiesObserver)
        viewModel.isEmpty.removeObserver(isEmptyObserver)
    }

    @Test
    fun `setting employeeId should fetch employee and records`() = runTest {
        // Given
        val employeeId = 1L
        val employee = EmployeeEntity(id = employeeId, name = "John")
        val expectedRecords = listOf(
            EmployeeAttendanceRecord(
                1,
                employeeId,
                "",
                LocalDate.of(2024, 7, 1),
                0L,
                AttendanceStatus.ABSENT
            ),
            EmployeeAttendanceRecord(
                2,
                employeeId,
                "",
                LocalDate.of(2024, 7, 2),
                0L,
                AttendanceStatus.ABSENT
            ),
            EmployeeAttendanceRecord(
                3,
                employeeId,
                "",
                LocalDate.of(2024, 7, 3),
                0L,
                AttendanceStatus.ABSENT
            ),
        )
        every { preferencesGateway.getMonthStartDay() } returns 1
        coEvery { employeeRepo.getEmployeeById(employeeId) } returns employee
        coEvery {
            getEmployeeAttendanceRecordsUseCase(
                eq(employeeId),
                any(),
                any()
            )
        } returns expectedRecords
        // When
        viewModel.setEmployeeId(employeeId)

        advanceUntilIdle()

        // Then
        assertEquals(employee, viewModel.selectedEmployee.value)
        assertEquals(0, viewModel.presentCount.value)
        assertEquals(3, viewModel.absentCount.value)
    }

    @Test
    fun `setEmployeeId fetches employee and returns correct counters`() = runTest {
        val employeeId = 1L
        val employee = EmployeeEntity(id = employeeId, name = "John")
        val expectedRecords = listOf(
            EmployeeAttendanceRecord(1, employeeId, "", LocalDate.of(2025, 6, 1), 0L, AttendanceStatus.ABSENT),
            EmployeeAttendanceRecord(2, employeeId, "", LocalDate.of(2025, 6, 2), 0L, AttendanceStatus.ABSENT),
            EmployeeAttendanceRecord(3, employeeId, "", LocalDate.of(2025, 6, 3), 0L, AttendanceStatus.ABSENT),
            EmployeeAttendanceRecord(4, employeeId, "", LocalDate.of(2025, 6, 4), 0L, AttendanceStatus.PRESENT)
        )

        coEvery { employeeRepo.getEmployeeById(employeeId) } returns employee
        coEvery { getEmployeeAttendanceRecordsUseCase(employeeId, any(), any()) } returns expectedRecords

        viewModel.setEmployeeId(employeeId)
        advanceUntilIdle()

        assertEquals(employee, viewModel.selectedEmployee.value)
        assertEquals(1, viewModel.presentCount.value)
        assertEquals(3, viewModel.absentCount.value)
        val isEmptyValue = viewModel.isEmpty.value

        assertEquals(false, isEmptyValue)
    }

    @Test
    fun `setMonthRange uses correct date range`() = runTest {
        val employeeId = 2L
        val employee = EmployeeEntity(id = employeeId, name = "Jane")
        val expectedRange = LocalDate.of(2024, 7, 1)..LocalDate.of(2024, 7, 31)
        val expectedRecords = listOf(
            EmployeeAttendanceRecord(1, employeeId, "", LocalDate.of(2024, 7, 1), 0L, AttendanceStatus.PRESENT)
        )

        coEvery { employeeRepo.getEmployeeById(employeeId) } returns employee
        coEvery {
            getEmployeeAttendanceRecordsUseCase(employeeId, expectedRange.start, expectedRange.endInclusive)
        } returns expectedRecords

        viewModel.setMonthRange(7, 2024)
        viewModel.setEmployeeId(employeeId)
        advanceUntilIdle()

        assertEquals(expectedRecords, viewModel.filteredRecords.value)
    }

    @Test
    fun `setCustomRange uses exact range`() = runTest {
        val employeeId = 3L
        val employee = EmployeeEntity(id = employeeId, name = "Sam")
        val start = LocalDate.of(2024, 8, 1)
        val end = LocalDate.of(2024, 8, 5)
        val expectedRecords = listOf(
            EmployeeAttendanceRecord(1, employeeId, "", start, 0L, AttendanceStatus.PRESENT),
            EmployeeAttendanceRecord(2, employeeId, "", start.plusDays(1), 0L, AttendanceStatus.ABSENT)
        )

        coEvery { employeeRepo.getEmployeeById(employeeId) } returns employee
        coEvery { getEmployeeAttendanceRecordsUseCase(employeeId, start, end) } returns expectedRecords

        viewModel.setCustomRange(start, end)
        viewModel.setEmployeeId(employeeId)
        advanceUntilIdle()
        assertEquals(expectedRecords, viewModel.filteredRecords.value)
    }

    @Test
    fun `toggleStatusFilter adds and removes status`() = runTest {
        viewModel.toggleStatusFilter(AttendanceStatus.PRESENT)
        assert(viewModel.isStatusSelected(AttendanceStatus.PRESENT))
        viewModel.toggleStatusFilter(AttendanceStatus.PRESENT)
        assert(!viewModel.isStatusSelected(AttendanceStatus.PRESENT))
    }

    @Test
    fun `clearFilters resets filters`() = runTest {
        viewModel.toggleStatusFilter(AttendanceStatus.ABSENT)
        assertTrue(viewModel.isStatusSelected(AttendanceStatus.ABSENT))

        viewModel.clearFilters()
        assert(!viewModel.isStatusSelected(AttendanceStatus.ABSENT))
    }
    @Test
    fun `fetchRecordsForEmployee updates counters`() = runTest {
        val employeeId = 4L
        val employee = EmployeeEntity(id = employeeId, name = "Alex")
        val range = LocalDate.of(2024, 9, 1)..LocalDate.of(2024, 9, 3)
        val records = listOf(
            EmployeeAttendanceRecord(1, employeeId, "", range.start, 0L, AttendanceStatus.PRESENT),
            EmployeeAttendanceRecord(
                2,
                employeeId,
                "",
                range.start.plusDays(1),
                10L,
                AttendanceStatus.LATE
            ),
            EmployeeAttendanceRecord(
                3,
                employeeId,
                "",
                range.endInclusive,
                0L,
                AttendanceStatus.ABSENT
            ),
            EmployeeAttendanceRecord(
                4,
                employeeId,
                "",
                range.endInclusive,
                0L,
                AttendanceStatus.EXTRA_DAY
            )
        )
        coEvery { employeeRepo.getEmployeeById(employeeId) } returns employee
        coEvery {
            getEmployeeAttendanceRecordsUseCase(
                employeeId,
                any(),
                any()
            )
        } returns records
        viewModel.setEmployeeId(employeeId)
        viewModel.setCustomRange(range.start, range.endInclusive)
        advanceUntilIdle()
        assertEquals(3, viewModel.presentCount.value)
        assertEquals(1, viewModel.absentCount.value)
        assertEquals(1, viewModel.extraDaysCount.value)
        assertEquals(10L, viewModel.tardiesCount.value)
    }

    @Test
    fun `fetchEmployeeById updates selectedEmployee`() = runTest {
        val employeeId = 5L
        val employee = EmployeeEntity(id = employeeId, name = "Taylor")
        coEvery { employeeRepo.getEmployeeById(employeeId) } returns employee
        viewModel.fetchEmployeeById(employeeId)
        advanceUntilIdle()
        assertEquals(employee, viewModel.selectedEmployee.value)
    }

}
