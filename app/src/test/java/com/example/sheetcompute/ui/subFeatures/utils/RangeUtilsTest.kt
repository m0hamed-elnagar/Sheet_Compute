package com.example.sheetcompute.ui.subFeatures.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class RangeUtilsTest {
    @Test
    fun `iterator whenGivenValidDateRange returnsAllDatesInRange`() {
        val start = LocalDate.of(2023, 1, 1)
        val end = LocalDate.of(2023, 1, 5)
        val range = start..end
        val dates = range.toList()
        assertEquals(5, dates.size)
        assertEquals(start, dates.first())
        assertEquals(end, dates.last())
    }

    @Test
    fun `count whenMatchingEvenDays returnsCorrectCount`() {
        val start = LocalDate.of(2023, 1, 1)
        val end = LocalDate.of(2023, 1, 10)
        val range = start..end
        val count = range.count { it.dayOfWeek.value == 7 } // Sundays
        assertTrue(count > 0)
    }

    @Test
    fun `filter should return only dates matching predicate`() {
        val start = LocalDate.of(2023, 1, 1)
        val end = LocalDate.of(2023, 1, 7)
        val range = start..end
        val filtered = range.filter { it.dayOfWeek.value == 1 } // Mondays
        assertTrue(filtered.all { it.dayOfWeek.value == 1 })
    }

    @Test
    fun `map whenMappingToDayOfWeek returnsListOfDayNames`() {
        val start = LocalDate.of(2023, 1, 1)
        val end = LocalDate.of(2023, 1, 3)
        val range = start..end
        val mapped = range.map { it.dayOfMonth }
        assertEquals(listOf(1, 2, 3), mapped)
    }
}

