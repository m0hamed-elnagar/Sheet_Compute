package com.example.sheetcompute.domain.excel

import io.mockk.every
import io.mockk.mockk
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalTime

class CalcClockInTest {



    @Test
    fun `returns correct LocalTime for numeric cell`() {
        val cell = mockk<Cell>()
        every { cell.cellTypeEnum } returns CellType.NUMERIC
        every { cell.numericCellValue } returns 0.5  // 12:00 PM

        val result = calcClockIn(cell)
        assertEquals(LocalTime.of(12, 0), result)
    }

    @Test
    fun `returns correct LocalTime for valid string cell`() {
        val cell = mockk<Cell>()
        every { cell.cellTypeEnum } returns CellType.STRING
        every { cell.stringCellValue } returns "08:30"

        val result = calcClockIn(cell)
        assertEquals(LocalTime.of(8, 30), result)
    }

    @Test
    fun `parse string cell with valid time format`() {
        val cell = mockk<Cell>()
        every { cell.cellTypeEnum } returns CellType.STRING
        every { cell.stringCellValue } returns "09:30:15"

        val result = calcClockIn(cell)
        assertEquals(LocalTime.of(9, 30, 15), result)
    }

    @Test
    fun `returns null for invalid string format`() {
        val cell = mockk<Cell>()
        every { cell.cellTypeEnum } returns CellType.STRING
        every { cell.stringCellValue } returns "not a time"

        val result = calcClockIn(cell)
        assertNull(result)
    }

    @Test
    fun `returns null for unexpected cell type`() {
        val cell = mockk<Cell>()
        every { cell.cellTypeEnum } returns CellType.BOOLEAN

        val result = calcClockIn(cell)
        assertNull(result)
    }

    @Test
    fun `returns null for exception in numeric parsing`() {
        val cell = mockk<Cell>()
        every { cell.cellTypeEnum } returns CellType.NUMERIC
        every { cell.numericCellValue } throws RuntimeException("Simulated failure")

        val result = calcClockIn(cell)
        assertNull(result)
    }

    @Test
    fun `returns null for exception in cell type access`() {
        val cell = mockk<Cell>()
        every { cell.cellTypeEnum } throws RuntimeException("Simulated failure")

        val result = calcClockIn(cell)
        assertNull(result)
    }
}
