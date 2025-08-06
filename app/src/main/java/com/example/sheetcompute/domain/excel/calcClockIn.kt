package com.example.sheetcompute.domain.excel

import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.parseTimeString
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import java.time.LocalTime

fun calcClockIn(
    timeCell: Cell,
): LocalTime? {
     val formatter = DataFormatter()

    return try {
        when (// Universal approach that works for all POI versions
            timeCell.cellTypeEnum) {
            CellType.NUMERIC -> {

                val excelTimeValue = timeCell.numericCellValue
                LocalTime.ofNanoOfDay((excelTimeValue * 24 * 60 * 60 * 1_000_000_000).toLong())
            }

            CellType.STRING -> {
                val timeStr = timeCell.stringCellValue.trim()

                // Try parsing normally
                parseTimeString(timeStr) ?: run {
                    // Try parsing as Excel-style time fraction
                    val excelTimeValue = timeStr.toDoubleOrNull()
                    excelTimeValue?.let {
                        LocalTime.ofNanoOfDay((it * 24 * 60 * 60 * 1_000_000_000).toLong())
                    }
                }
            }

            else -> {
                val timeStr = formatter.formatCellValue(timeCell).trim()
                parseTimeString(timeStr) ?: return null
            }
        }
    } catch (e: Exception) {

        null
    }
}
