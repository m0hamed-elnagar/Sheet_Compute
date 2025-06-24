package com.example.sheetcompute.domain.gateways.database.daos

import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceSummary
import java.time.LocalDate

class DummyAttendanceDao : AttendanceDao {
    private val attendanceRecords = mutableListOf<AttendanceRecord>()
    override suspend fun insertAll(records: List<AttendanceRecord>): List<Long> {
        attendanceRecords.addAll(records)
        return attendanceRecords.mapIndexed { index, _ -> index.toLong() }
    }

    override suspend fun getAllEmployeeIds(): List<Int> {
        return emptyList() // Dummy implementation
    }

    override suspend fun getEmployeeRecordsByDateList(
        employeeId: Long,
        dates: List<LocalDate>
    ): List<AttendanceRecord> {
        return emptyList() // Dummy implementation
    }

    override suspend fun addAttendanceRecord(attendanceRecord: AttendanceRecord) {
        // Dummy implementation
    }

    override suspend fun getAttendanceSummary(
        startDate: LocalDate,
        endDate: LocalDate,
        month: Int,
        year: Int,
        limit: Int,
        offset: Int
    ): List<AttendanceSummary> {
        return emptyList() // Dummy implementation
    }
}