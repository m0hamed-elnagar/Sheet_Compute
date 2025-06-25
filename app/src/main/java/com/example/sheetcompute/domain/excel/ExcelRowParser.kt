package com.example.sheetcompute.domain.excel

import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.formatTimeForStorage

import android.util.Log
import com.example.sheetcompute.entities.AttendanceRecord
import com.example.sheetcompute.entities.EmployeeEntity
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.parseDateSafely
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.parseTimeString
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import java.time.Duration
import java.time.LocalTime

object ExcelRowParser {
    private val formatter = DataFormatter()

    data class ParseResult(
        val employee: EmployeeEntity?,
        val record: AttendanceRecord?
    )

    fun parseRow(
        row: Row,
        workStartTime: LocalTime
    ): ParseResult? {
        val idCell = row.getCell(0)?.let { formatter.formatCellValue(it) } ?: return null
        val id = idCell.toIntOrNull() ?: return null
        val name = row.getCell(1)?.toString()?.trim() ?: return null
        val dateStr = row.getCell(2)?.toString()?.trim() ?: return null
        val timeCell = row.getCell(3)
        var timeStr = timeCell?.toString()?.trim() ?: return null

        Log.d(
            "ExcelImport",
            "Parsing row ${row.rowNum}: id=$id, name=$name, dateStr=$dateStr, timeStr=$timeStr"
        )

        if (timeStr.isEmpty()) {
            Log.w("ExcelRowParser", "Skipping row ${row.rowNum} due to invalid timeStr: $timeStr")
            return null
        }

        val employee = EmployeeEntity(id, name)
        val date = parseDateSafely(dateStr) ?: return null
        val clockIn = calcClockIn(timeCell)?: return null
        val tardy = if (clockIn.isAfter(workStartTime)) {
            Duration.between(workStartTime, clockIn).toMinutes()
        } else 0
        val record = AttendanceRecord(
            employeeId = id.toString(),
            date = date,
            clockIn = formatTimeForStorage(clockIn),
            tardyMinutes = tardy
        )
        return ParseResult(employee, record)
    }

    private fun calcClockIn(
        timeCell: Cell,
    ): LocalTime? {
        return try {
            when (// Universal approach that works for all POI versions
                timeCell.cellTypeEnum) {
                CellType.NUMERIC -> {

                    val excelTimeValue = timeCell.numericCellValue
                    Log.d("ExcelImport", "Parsed numeric time value: $excelTimeValue")
                    LocalTime.ofNanoOfDay((excelTimeValue * 24 * 60 * 60 * 1_000_000_000).toLong())
                }

                CellType.STRING -> {
                    val timeStr = timeCell.stringCellValue.trim()
                    parseTimeString(timeStr) ?: return null
                }

                else -> {
                    val timeStr = formatter.formatCellValue(timeCell).trim()
                    parseTimeString(timeStr) ?: return null
                }
            }
        } catch (e: Exception) {
            Log.w("ExcelRowParser", "Failed to parse time cell: ${e.message}", e)
            null
        }
    }
}


