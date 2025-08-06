package com.example.sheetcompute.ui.subFeatures.utils


import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class ExcelFileSaverTest {
    @Test
    fun `generateFileName should include base and timestamp`() {
        val base = "testfile"
        val fileName = ExcelFileSaver.generateFileName(base)
        assertTrue(fileName.startsWith("$base-"))
        assertTrue(fileName.endsWith(".xls"))
        // Check timestamp format (yyyy-MM-dd_HH-mm)
        val regex = Regex("$base-\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}\\.xls")
        assertTrue(regex.matches(fileName))
    }

    @Test
    fun `generateFileName should be unique for different times`() {
        val base = "unique"

        // Simulate first call at 00:00
        ExcelFileSaver.nowProvider = { Date(0) }
        val first = ExcelFileSaver.generateFileName(base)

        // Simulate second call at 00:02 (2 minutes later)
        ExcelFileSaver.nowProvider = { Date(2 * 60 * 1000) }
        val second = ExcelFileSaver.generateFileName(base)

        assertNotEquals(first, second)
    }



}

