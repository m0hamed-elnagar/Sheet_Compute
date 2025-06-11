package com.example.sheetcompute.domain.useCases


import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CustomMonthKtTest {


    @Test
    fun `should return full year range when month is 0 and startDay is 1`() {
        val range = createCustomMonthRange(0, 2024, 1)

        assertEquals(LocalDate.of(2023, 12, 1), range?.start)
        assertEquals(LocalDate.of(2024, 12, 31), range?.endInclusive)
    }

    @Test
    fun `should return custom year range when month is 0 and startDay is 5`() {
        val range = createCustomMonthRange(0, 2024, 5)

        assertEquals(LocalDate.of(2023, 12, 5), range?.start)
        assertEquals(LocalDate.of(2024, 12, 4), range?.endInclusive)
    }

    @Test
    fun `should handle January correctly with custom startDay`() {
        val range = createCustomMonthRange(1, 2024, 5)

        assertEquals(LocalDate.of(2023, 12, 5), range?.start)
        assertEquals(LocalDate.of(2024, 1, 4), range?.endInclusive)
    }

    @Test
    fun `should handle February correctly with startDay 1`() {
        val range = createCustomMonthRange(2, 2024, 1)

        assertEquals(LocalDate.of(2024, 1, 1), range?.start)
        assertEquals(LocalDate.of(2024, 2, 29), range?.endInclusive) // Leap year check
    }

    @Test
    fun `should handle normal month like May with startDay 10`() {
        val range = createCustomMonthRange(5, 2024, 10)

        assertEquals(LocalDate.of(2024, 4, 10), range?.start)
        assertEquals(LocalDate.of(2024, 5, 9), range?.endInclusive)
    }
}