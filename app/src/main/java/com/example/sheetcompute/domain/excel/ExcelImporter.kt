package com.example.sheetcompute.domain.excel

import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.data.repo.EmployeeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.time.LocalTime

object ExcelImporter {
    private const val EMPLOYEE_CHUNK_SIZE = 100
    private const val RECORD_CHUNK_SIZE = 500

    data class ImportResult(
        val newEmployees: Int,
        val recordsAdded: Int,
        val duplicates: List<AttendanceRecord>,
        val errors: List<ParseResult.Error>
    )

    suspend fun import(
        inputStream: InputStream,
        workStartTime: LocalTime = PreferencesGateway.getWorkStartTime(),
        employeeRepo: EmployeeRepo,
        attendanceRepo: AttendanceRepo
    ): ImportResult = withContext(Dispatchers.IO) {
        val result = ExcelParser.parse(inputStream, workStartTime)

        val existingIds = employeeRepo.getAllEmployeeIds().toSet()
        val newEmployees = result.employees.filter { it.id !in existingIds }

        newEmployees.chunked(EMPLOYEE_CHUNK_SIZE).forEach {
            employeeRepo.insertEmployees(it)
        }

        var insertedCount = 0
        val duplicates = mutableListOf<AttendanceRecord>()
        result.records.chunked(RECORD_CHUNK_SIZE).forEach {
             val inserted = attendanceRepo.insertRecords(it)
            insertedCount += inserted.addedCount
            duplicates.addAll(inserted.skippedRecords)
        }

        ImportResult(
            newEmployees = newEmployees.size,
            recordsAdded = insertedCount,
            duplicates = duplicates,
            errors = result.errors
        )
    }
}
