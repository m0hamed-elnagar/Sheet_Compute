package com.example.sheetcompute.domain.excel

import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.data.repo.EmployeeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.InputStream
import java.time.LocalTime


object ExcelImporter {
    private const val EMPLOYEE_CHUNK_SIZE = 100
    private const val RECORD_CHUNK_SIZE = 500

    data class ImportResult(
        val newEmployees: Int,
        val recordsAdded: Int,
    )

    suspend fun import(
        inputStream: InputStream,
        workStartTime: LocalTime = PreferencesGateway.getWorkStartTime(),
        employeeRepo: EmployeeRepo,
        attendanceRepo: AttendanceRepo
    ): ImportResult = withContext(Dispatchers.IO) {
        val (employees, records) = parseExcel(inputStream, workStartTime)
        val existingIds = employeeRepo.getAllEmployeeIds().toSet()
        val newEmployees = employees.filter { it.id !in existingIds }

        // Insert employees in chunks
        for (chunk in newEmployees.chunked(EMPLOYEE_CHUNK_SIZE)) {
            employeeRepo.insertEmployees(chunk)
        }

        var insertedRecords = 0
        for (chunk in records.chunked(RECORD_CHUNK_SIZE)) {
            insertedRecords += attendanceRepo.insertRecords(chunk)
        }

        ImportResult(newEmployees.size, insertedRecords)
    }

    private fun parseExcel(
        inputStream: InputStream,
        workStartTime: LocalTime
    ): Pair<Set<EmployeeEntity>, List<AttendanceRecord>> {
        val employees = mutableSetOf<EmployeeEntity>()
        val records = mutableListOf<AttendanceRecord>()
        HSSFWorkbook(inputStream).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            for (row in sheet) {
                if (row.rowNum == 0) continue
                val result = ExcelRowParser.parseRow(row, workStartTime)
                result?.employee?.let { employees.add(it) }
                result?.record?.let { records.add(it) }
            }
        }
        return employees to records
    }
}
