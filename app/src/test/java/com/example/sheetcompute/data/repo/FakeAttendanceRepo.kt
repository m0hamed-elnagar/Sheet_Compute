package com.example.sheetcompute.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FakeAttendanceRepo : AttendanceRepoInterface {
    var attendanceRecords = mutableListOf<AttendanceRecord>()
    var attendanceRecordsLiveData = MutableLiveData< List<AttendanceRecord>>(attendanceRecords)
    var insertResult: InsertResult? = null
    var pagedSummaries: Pager<Int, AttendanceRecordUI>? = null

    override suspend fun getEmployeeAttendanceRecordsByRange(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<AttendanceRecord> {
        return attendanceRecords.filter {
            it.employeeId == employeeId &&
            !it.date.isBefore(startDate) &&
            !it.date.isAfter(endDate)
        }
    }

    override suspend fun insertRecords(records: List<AttendanceRecord>): InsertResult {
        attendanceRecords.addAll(records)
        attendanceRecordsLiveData.postValue(attendanceRecords)
        return insertResult ?: InsertResult(records.size, emptyList())
    }
fun observeAttendanceRecords(): LiveData<List<AttendanceRecord>> {
    return attendanceRecordsLiveData
}
    override fun getPagedAttendanceSummaries(
        month: Int,
        year: Int,
        range: ClosedRange<LocalDate>,
        totalWorkingDays: Int,
        pageSize: Int
    ): Pager<Int, AttendanceRecordUI> {
        return pagedSummaries ?: Pager(PagingConfig(pageSize = pageSize)) { error("Not implemented") }
    }
}

