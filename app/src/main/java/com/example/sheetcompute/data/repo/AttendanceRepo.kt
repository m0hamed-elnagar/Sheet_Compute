package com.example.sheetcompute.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.paging.AttendanceSummaryPagingSource
import com.example.sheetcompute.data.local.room.AppDatabase
import java.time.LocalDate

class AttendanceRepo {
    private val database by lazy { AppDatabase.get() }

    private val attendanceDao by lazy { database.EmployeeAttendanceDao() }
    suspend fun getEmployeeAttendanceRecordsByRange(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<AttendanceRecord>{
        return attendanceDao.getEmployeeRecordsByDateRange(employeeId, startDate, endDate)


    }

    suspend fun insertRecords(records: List<AttendanceRecord>):Int{ return attendanceDao.insertAll(records).count { it != -1L }}
fun getPagedAttendanceSummaries(
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