package com.example.sheetcompute.domain.excel



import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity

sealed class ParseResult {
    data class Success(
        val employee: EmployeeEntity,
        val record: AttendanceRecord
    ) : ParseResult()

    data class Error(
        val rowNumber: Int,
        val reason: String,
        val rowContent: List<String>
    ) : ParseResult()
}
