package com.example.sheetcompute.domain.excel.export

import com.example.sheetcompute.domain.excel.ParseResult
import org.apache.poi.hssf.usermodel.HSSFWorkbook
object RejectionWorkbookBuilder {

    fun buildWorkbook(errors: List<ParseResult.Error>): HSSFWorkbook {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Rejected Rows")

    // Header
    val header = sheet.createRow(0)
    header.createCell(0).setCellValue("Row Number")
    header.createCell(1).setCellValue("Reason")
    header.createCell(2).setCellValue("ID")
    header.createCell(3).setCellValue("Name")
    header.createCell(4).setCellValue("Date")
    header.createCell(5).setCellValue("Time")

        sheet.setColumnWidth(0, 15 * 256)
        sheet.setColumnWidth(1, 40 * 256)
        sheet.setColumnWidth(2, 15 * 256)
        sheet.setColumnWidth(3, 50 * 256)
        sheet.setColumnWidth(4, 20 * 256)
        sheet.setColumnWidth(5, 20 * 256)

        for ((index, error) in errors.withIndex()) {
        val row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(error.rowNumber.toDouble())
        row.createCell(1).setCellValue(error.reason)
        error.rowContent.forEachIndexed { i, value ->
            row.createCell(i + 2).setCellValue(value)
        }
    }


        return workbook
    }
}
