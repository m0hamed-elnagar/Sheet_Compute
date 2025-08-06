package com.example.sheetcompute.domain.excel

import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalTime

class ExcelParserTest {
    @Test
    fun `parse returns correct bundle for valid input`() {
        // Create a workbook in memory
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet()
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("ID")
        header.createCell(1).setCellValue("Name")
        header.createCell(2).setCellValue("Date")
        header.createCell(3).setCellValue("ClockIn")
        val row = sheet.createRow(1)
        row.createCell(0).setCellValue(1.0)
        row.createCell(1).setCellValue("John Doe")
        row.createCell(2).setCellValue("2023-08-03")
        row.createCell(3).setCellValue("08:00")
        val out = ByteArrayOutputStream()
        workbook.write(out)
        val input = ByteArrayInputStream(out.toByteArray())
        val result = ExcelParser.parse(input, LocalTime.of(8, 0))
        assertEquals(1, result.employees.size)
        assertEquals(1, result.records.size)
        assertEquals(0, result.errors.size)
    }

    @Test
    fun `parse returns error for invalid row`() {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet()
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("ID")
        header.createCell(1).setCellValue("Name")
        header.createCell(2).setCellValue("Date")
        header.createCell(3).setCellValue("ClockIn")
        val row = sheet.createRow(1)
        row.createCell(0).setCellValue(1.0)
        row.createCell(1).setCellValue("") // Missing name
        row.createCell(2).setCellValue("2023-08-03")
        row.createCell(3).setCellValue("08:00")
        val out = ByteArrayOutputStream()
        workbook.write(out)
        val input = ByteArrayInputStream(out.toByteArray())
        val result = ExcelParser.parse(input, LocalTime.of(8, 0))
        assertEquals(1, result.errors.size)
    }
}

