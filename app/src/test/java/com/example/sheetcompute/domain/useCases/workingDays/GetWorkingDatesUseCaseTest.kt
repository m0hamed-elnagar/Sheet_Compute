package com.example.sheetcompute.domain.useCases.workingDays

import com.example.sheetcompute.domain.useCases.workingDays.GetNonWorkingDaysUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetWorkingDatesUseCaseTest {
    private val getNonWorkingDaysUseCase = mockk<GetNonWorkingDaysUseCase>()
    private val useCase = GetWorkingDatesUseCase(getNonWorkingDaysUseCase)

    @Test
    fun `returns only working days`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 5)
        val nonWorking = setOf(
            LocalDate.of(2024, 7, 2),
            LocalDate.of(2024, 7, 4)
        )
        coEvery { getNonWorkingDaysUseCase(start, end) } returns nonWorking
        val result = useCase.invoke(start, end)
        val expected = listOf(
            LocalDate.of(2024, 7, 1),
            LocalDate.of(2024, 7, 3),
            LocalDate.of(2024, 7, 5)
        )
        assertEquals(expected, result)
    }

    @Test
    fun `returns empty list when all days are non-working`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 3)
        val nonWorking = setOf(
            LocalDate.of(2024, 7, 1),
            LocalDate.of(2024, 7, 2),
            LocalDate.of(2024, 7, 3)
        )
        coEvery { getNonWorkingDaysUseCase(start, end) } returns nonWorking
        val result = useCase.invoke(start, end)
        assertEquals(emptyList<LocalDate>(), result)
    }

    @Test
    fun `returns all days when no non-working days`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 3)
        coEvery { getNonWorkingDaysUseCase(start, end) } returns emptySet()
        val result = useCase.invoke(start, end)
        val expected = listOf(
            LocalDate.of(2024, 7, 1),
            LocalDate.of(2024, 7, 2),
            LocalDate.of(2024, 7, 3)
        )
        assertEquals(expected, result)
    }
}
