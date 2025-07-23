package com.example.sheetcompute.viewModels

import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.HolidayRepo
import com.example.sheetcompute.ui.features.holidaysCalendar.CalendarViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@ExperimentalCoroutinesApi
class CalendarViewModelTest {
    private lateinit var holidayRepo: HolidayRepo
    private lateinit var preferencesGateway: PreferencesGateway
    private lateinit var viewModel: CalendarViewModel
    private val dummyHolidays = listOf(
        Holiday(1,  LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1),"New Year"),
        Holiday(2, LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 6), "Holiday 2"),
        Holiday(3, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), "Holiday 3"),
    )
    @Before
    fun setUp() {
        holidayRepo = mockk(relaxed = true)
        preferencesGateway = mockk(relaxed = true)
        every { preferencesGateway.weekendDays } returns MutableStateFlow(setOf(1, 7))
        coEvery { holidayRepo.getHolidaysByDateRange(any(), any()) } returns dummyHolidays
        viewModel = CalendarViewModel(holidayRepo, preferencesGateway)
    }


    @Test
    fun `loadInitialData calls preferencesGateway init and preloads months`() = runTest {
        coVerify { preferencesGateway.init() }
    }

    @Test
    fun `addHoliday calls repo and reloads month`() = runTest {
        val holiday = Holiday(1, LocalDate.now(), LocalDate.now(), "Test")
        viewModel.addHoliday(holiday)
        coVerify { holidayRepo.addHoliday(holiday) }
    }

    @Test
    fun `updateHoliday calls repo and reloads month`() = runTest {
        val holiday = Holiday(2, LocalDate.now(), LocalDate.now(), "Test")
        viewModel.updateHoliday(holiday)
        coVerify { holidayRepo.updateHoliday(holiday) }
    }

    @Test
    fun `deleteHoliday calls repo and reloads month`() =runTest {
        val holiday = Holiday(3, LocalDate.now(), LocalDate.now(), "Test")
        viewModel.deleteHoliday(holiday)
        coVerify { holidayRepo.deleteHoliday(holiday) }
    }

    @Test
    fun `updateWeekendDays calls preferencesGateway`() = runTest {
        viewModel.updateWeekendDays(setOf(java.time.DayOfWeek.SATURDAY))
        coVerify { preferencesGateway.setWeekendDays(any()) }
    }

    @Test
    fun `updateCurrentMonth updates state and loads holidays`() =runTest {
        val month = YearMonth.of(2025, 7)
        viewModel.updateCurrentMonth(month)
        assertEquals(month, viewModel.currentMonth.value)
    }
    @Test
    fun `preloadMonths loads holidays for multiple months`() = runTest {
        val months = (-5..5).map { YearMonth.now().plusMonths(it.toLong()) }
        months.forEach {
            viewModel.loadHolidaysForMonth(it)
            coVerify { holidayRepo.getHolidaysByDateRange(it.atDay(1), it.atEndOfMonth()) }
        }
    }
    @Test
    fun `holidaysForCurrentMonth emits correct holidays`() = runTest {
        val targetMonth = YearMonth.of(2025, 1)
        viewModel.updateCurrentMonth(targetMonth)
        assertEquals(dummyHolidays, viewModel.holidaysForCurrentMonth.value)
    }

}