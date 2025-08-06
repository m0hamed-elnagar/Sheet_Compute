package com.example.sheetcompute.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.local.room.daos.AttendanceDao
import com.example.sheetcompute.data.paging.AttendanceSummaryPagingSource
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class InsertResult(
    val addedCount: Int,
    val skippedRecords: List<AttendanceRecord>
)
interface AttendanceRepoInterface {
    suspend fun getEmployeeAttendanceRecordsByRange(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<AttendanceRecord>

    suspend fun insertRecords(records: List<AttendanceRecord>): InsertResult

    fun getPagedAttendanceSummaries(
        month: Int,
        year: Int,
        range: ClosedRange<LocalDate>,
        totalWorkingDays: Int,
        pageSize: Int,
    ): Pager<Int, AttendanceRecordUI>
}
@Singleton

class AttendanceRepo @Inject constructor(
    private val attendanceDao: AttendanceDao,
): AttendanceRepoInterface {

    override suspend fun getEmployeeAttendanceRecordsByRange(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<AttendanceRecord> {
        return attendanceDao.getEmployeeRecordsByDateRange(employeeId, startDate, endDate)


    }

    override suspend fun insertRecords(records: List<AttendanceRecord>): InsertResult{
        val insertResults = attendanceDao.insertAll(records)

        var added = 0
        val duplicates = mutableListOf<AttendanceRecord>()

        insertResults.forEachIndexed { index, result ->
            if (result != -1L) {
                added++
            } else {
                duplicates.add(records[index])
            }
        }

        return InsertResult(
            addedCount = added,
            skippedRecords = duplicates
        )    }

    override fun getPagedAttendanceSummaries(
        month: Int,
        year: Int,
        range: ClosedRange<LocalDate>,
        totalWorkingDays: Int,
        pageSize: Int,
    ): Pager<Int, AttendanceRecordUI> {
        return Pager(
            config = PagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
                AttendanceSummaryPagingSource(
                    dao = attendanceDao,
                    month = month,
                    year = year,
                    range = range,
                    totalWorkingDays = totalWorkingDays,
                    pageSize = pageSize
                )
            }
        )

    }
}