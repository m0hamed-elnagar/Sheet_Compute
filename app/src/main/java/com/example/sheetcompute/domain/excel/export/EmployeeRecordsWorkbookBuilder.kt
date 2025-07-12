package com.example.sheetcompute.domain.excel.export

import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.time.format.DateTimeFormatter

object EmployeeRecordsWorkbookBuilder {
    fun buildWorkbook(employees: List<EmployeeEntity>, records: List<EmployeeAttendanceRecord>): HSSFWorkbook {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Employees Records")

        // Header
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("Employee ID")
        header.createCell(1).setCellValue("Name")
        header.createCell(2).setCellValue("Date")
        header.createCell(3).setCellValue("Clock In")
        header.createCell(4).setCellValue("Tardy Minutes")
        header.createCell(5).setCellValue("Status")


        sheet.setColumnWidth(0, 15 * 256)
        sheet.setColumnWidth(1, 30 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 20 * 256)
        sheet.setColumnWidth(4, 15 * 256)
        sheet.setColumnWidth(5, 20 * 256)

        // Map employeeId to EmployeeEntity for quick lookup
        val employeeMap = employees.associateBy { it.id }

        // Fill rows
        records.forEachIndexed { index, record ->
            val row = sheet.createRow(index + 1)
            val employee = employeeMap[record.employeeId]
            row.createCell(0).setCellValue(record.employeeId.toString())
            row.createCell(1).setCellValue(employee?.name ?: "")
            row.createCell(2).setCellValue(record.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
            row.createCell(3).setCellValue(record.loginTime)
            row.createCell(4).setCellValue(record.lateDuration.toDouble())
            row.createCell(5).setCellValue(record.status.name)
        }

        return workbook
    }
}
