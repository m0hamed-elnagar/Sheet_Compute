package com.example.sheetcompute.domain.excel

import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.InputStream
import java.time.LocalTime

object ExcelParser {
    data class ParseResultBundle(
        val employees: Set<EmployeeEntity>,
        val records: List<AttendanceRecord>,
        val errors: List<ParseResult.Error>
    )

    fun parse(
        inputStream: InputStream,
        workStartTime: LocalTime
    ): ParseResultBundle {
        val employees = mutableSetOf<EmployeeEntity>()
        val records = mutableListOf<AttendanceRecord>()
        val errors = mutableListOf<ParseResult.Error>()

        HSSFWorkbook(inputStream).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            for (row in sheet) {
                if (row.rowNum == 0) continue

                when (val result = ExcelRowParser.parseRow(row, workStartTime)) {
                    is ParseResult.Success -> {
                        employees.add(result.employee)
                        records.add(result.record)
                    }
                    is ParseResult.Error -> errors.add(result)
                    else  -> continue // Skip null results
                }
            }
        }

        return ParseResultBundle(employees, records, errors)
    }
}
