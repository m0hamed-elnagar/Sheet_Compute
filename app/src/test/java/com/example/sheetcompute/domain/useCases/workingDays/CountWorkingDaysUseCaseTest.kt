package com.example.sheetcompute.domain.useCases.workingDays

import com.example.sheetcompute.domain.useCases.workingDays.GetNonWorkingDaysUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CountWorkingDaysUseCaseTest {
    private val getNonWorkingDaysUseCase = mockk<GetNonWorkingDaysUseCase>()
    private val useCase = CountWorkingDaysUseCase(getNonWorkingDaysUseCase)

    @Test
    fun `counts only working days`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 5)
        val nonWorking = setOf(
            LocalDate.of(2024, 7, 2),
            LocalDate.of(2024, 7, 4)
        )
        coEvery { getNonWorkingDaysUseCase(start, end) } returns nonWorking
        val result = useCase.invoke(start, end)
        assertEquals(3, result) // 1, 3, 5 are working days
    }

    @Test
    fun `returns zero when all days are non-working`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 3)
        val nonWorking = setOf(
            LocalDate.of(2024, 7, 1),
            LocalDate.of(2024, 7, 2),
            LocalDate.of(2024, 7, 3)
        )
        coEvery { getNonWorkingDaysUseCase(start, end) } returns nonWorking
        val result = useCase.invoke(start, end)
        assertEquals(0, result)
    }

    @Test
    fun `returns total days when no non-working days`() = runBlocking {
        val start = LocalDate.of(2024, 7, 1)
        val end = LocalDate.of(2024, 7, 3)
        coEvery { getNonWorkingDaysUseCase(start, end) } returns emptySet()
        val result = useCase.invoke(start, end)
        assertEquals(3, result)
    }
}
