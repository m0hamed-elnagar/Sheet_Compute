package com.example.sheetcompute.domain.excel

import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.formatTimeForStorage

import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.parseDateSafely
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import java.time.Duration
import java.time.LocalTime

object ExcelRowParser {
    private val formatter = DataFormatter()

    fun parseRow(row: Row, workStartTime: LocalTime): ParseResult? {
        val rowNum = row.rowNum + 1
        val content = row.map { formatter.formatCellValue(it).trim() }
        try {
            val idStr = content.getOrNull(0)?.takeIf { it.isNotBlank() } ?: return ParseResult.Error(rowNum, "Missing ID", content)
            val id = idStr.toLongOrNull() ?: return ParseResult.Error(rowNum, "Invalid ID format", content)

            val name = content.getOrNull(1)?.takeIf { it.isNotBlank() } ?: return ParseResult.Error(rowNum, "Missing name", content)
            val dateStr = content.getOrNull(2)?.takeIf { it.isNotBlank() } ?: return ParseResult.Error(rowNum, "Missing date", content)

            val date = parseDateSafely(dateStr.trim()) ?: return ParseResult.Error(rowNum, "Invalid date", content)
            val clockIn = calcClockIn(row.getCell(3)) ?: return ParseResult.Error(rowNum, "Invalid time", content)

            val tardyMinutes = if (clockIn.isAfter(workStartTime)) {
                Duration.between(workStartTime, clockIn).toMinutes()
            } else 0

            val employee = EmployeeEntity(id = id, name = name)
            val record = AttendanceRecord(employeeId = id, date = date, clockIn = formatTimeForStorage(clockIn), tardyMinutes = tardyMinutes)

            return ParseResult.Success(employee, record)
        } catch (e: Exception) {
            return ParseResult.Error(rowNum, "Exception: ${e.message}", content)
        }
    }
}
