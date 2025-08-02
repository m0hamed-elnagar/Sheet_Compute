package com.example.sheetcompute.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sheetcompute.MainDispatcherRule
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.FakeHolidayRepo
import com.example.sheetcompute.data.repo.HolidayRepo
import com.example.sheetcompute.data.repo.HolidayRepoInterface
import com.example.sheetcompute.getOrAwaitValue
import com.example.sheetcompute.ui.features.holidaysCalendar.CalendarViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@ExperimentalCoroutinesApi
class CalendarViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    //    private lateinit var holidayRepo: HolidayRepoInterface
    private lateinit var holidayRepo: FakeHolidayRepo
    private lateinit var preferencesGateway: PreferencesGateway
    private lateinit var viewModel: CalendarViewModel
    private val dummyHolidays = listOf(
        Holiday(1, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1), "New Year"),
        Holiday(2, LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 6), "Holiday 2"),
        Holiday(3, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), "Holiday 3"),
    )

    @Before
    fun setUp() {
        holidayRepo = FakeHolidayRepo()
        holidayRepo.clear() // Ensure clean state before each test
        preferencesGateway = mockk(relaxed = true)
        every { preferencesGateway.weekendDays } returns MutableStateFlow(setOf(1, 7))
        viewModel = CalendarViewModel(holidayRepo, preferencesGateway)
    }


    @Test
    fun `loadInitialData calls preferencesGateway init and preloads months`() = runTest {
        viewModel.loadInitialData() // Explicitly call to trigger init
        coVerify { preferencesGateway.init() }
    }

    @Test
    fun `addHoliday adds to fake repo and reloads month`() = runTest {
        val holiday = Holiday(9, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2), "Test Holiday")
        viewModel.addHoliday(holiday)
        advanceUntilIdle()
        // Wait for the LiveData to update
        val holidays = holidayRepo.observeHolidays().getOrAwaitValue()
        assert(holidays.contains(holiday))
    }


    @Test
    fun `updateHoliday calls repo and reloads month`() = runTest {
        val holiday = Holiday(2, LocalDate.now(), LocalDate.now(), "Test")
        viewModel.addHoliday(holiday)
        val updated = holiday.copy(name = "Updated")
        viewModel.updateHoliday(updated)
        advanceUntilIdle()

        val holidays = holidayRepo.observeHolidays().getOrAwaitValue()
        assert(holidays.contains(updated))
    }

    @Test
    fun `deleteHoliday calls repo and reloads month`() = runTest {
        val holiday = Holiday(99, LocalDate.now(), LocalDate.now(), "Test")
        viewModel.addHoliday(holiday)
        viewModel.deleteHoliday(holiday)

        assert(viewModel.holidaysForCurrentMonth.value.none { it.id == holiday.id })

    }

    @Test
    fun `updateWeekendDays calls preferencesGateway`() = runTest {
        viewModel.updateWeekendDays(setOf(java.time.DayOfWeek.SATURDAY))
        advanceUntilIdle()
        coVerify { preferencesGateway.setWeekendDays(any()) }
    }

    @Test
    fun `updateCurrentMonth updates state and loads holidays`() = runTest {
        val month = YearMonth.of(2025, 7)
        viewModel.updateCurrentMonth(month)
        assertEquals(month, viewModel.currentMonth.value)
    }


    @Test
    fun `holidaysForCurrentMonth emits correct holidays`() = runTest {
        val targetMonth = YearMonth.of(2025, 1)
        viewModel.updateCurrentMonth(targetMonth)
        val emittedHolidays = viewModel.holidaysForCurrentMonth
            .first { it.isNotEmpty() }
        assertEquals(
            dummyHolidays.sortedBy { it.id }.map { it.copy(createdAt = 0) },
            emittedHolidays.sortedBy { it.id }.map { it.copy(createdAt = 0) }
        )
    }

}