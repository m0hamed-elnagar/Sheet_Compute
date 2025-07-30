package com.example.sheetcompute.domain.useCases.workingDays

import com.example.sheetcompute.data.entities.HolidayRange
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.HolidayRepo
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetNonWorkingDaysUseCaseTest {
    private val holidayRepo = mockk<HolidayRepo>()
    private val preferencesGateway = mockk<PreferencesGateway>()
    private val useCase = GetNonWorkingDaysUseCase(holidayRepo, preferencesGateway)

    @Test
    fun `returns weekends and holidays as non-working days`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 7)
        val weekends = setOf(java.util.Calendar.SATURDAY, java.util.Calendar.SUNDAY)
        every { preferencesGateway.weekendDays } returns MutableStateFlow(weekends)
        coEvery { holidayRepo.getHolidayDatesBetween(start, end) } returns listOf(
            HolidayRange(LocalDate.of(2024, 7, 4), LocalDate.of(2024, 7, 4))
        )
        val result = useCase.invoke(start, end)
        val expected = setOf(
            LocalDate.of(2024, 7, 4), // holiday
            LocalDate.of(2024, 7, 6), // Saturday
            LocalDate.of(2024, 7, 7)  // Sunday
        )
        assertEquals(expected, result)
    }

    @Test
    fun `returns only weekends as non-working when no holidays`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 7)
        val weekends = setOf(java.util.Calendar.SATURDAY, java.util.Calendar.SUNDAY)
        every { preferencesGateway.weekendDays } returns MutableStateFlow(weekends)
        coEvery { holidayRepo.getHolidayDatesBetween(start, end) } returns emptyList()
        val result = useCase.invoke(start, end)
        val expected = setOf(
            LocalDate.of(2024, 7, 6), // Saturday
            LocalDate.of(2024, 7, 7)  // Sunday
        )
        assertEquals(expected, result)
    }

    @Test
    fun `returns only holidays as non-working when no weekends`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 3)
        every { preferencesGateway.weekendDays } returns MutableStateFlow(emptySet())
        coEvery { holidayRepo.getHolidayDatesBetween(start, end) } returns listOf(
            HolidayRange(LocalDate.of(2024, 7, 2), LocalDate.of(2024, 7, 2))
        )
        val result = useCase.invoke(start, end)
        val expected = setOf(LocalDate.of(2024, 7, 2))
        assertEquals(expected, result)
    }

    @Test
    fun `returns empty set when no holidays and no weekends`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 3)
        every { preferencesGateway.weekendDays } returns MutableStateFlow(emptySet())
        coEvery { holidayRepo.getHolidayDatesBetween(start, end) } returns emptyList()
        val result = useCase.invoke(start, end)
        assertEquals(emptySet<LocalDate>(), result)
    }
}
