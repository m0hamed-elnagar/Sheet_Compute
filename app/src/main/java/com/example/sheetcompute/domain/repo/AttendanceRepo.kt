package com.example.sheetcompute.domain.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.domain.database.paging.AttendanceSummaryPagingSource
import com.example.sheetcompute.domain.database.paging.EmployeeAttendanceRecordsPagingSource
import com.example.sheetcompute.domain.database.roomDB.AppDatabase
import java.time.LocalDate

class AttendanceRepo {
    private val database by lazy { AppDatabase.get() }

    private val attendanceDao by lazy { database.EmployeeAttendanceDao() }
    fun getEmployeeAttendanceRecordsByRange(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        holidays: List<LocalDate>
    ): Pager<Int, AttendanceRecord> {

        return Pager(
            config = PagingConfig(
                pageSize = 4,
                enablePlaceholders = true,
                maxSize = 12
            ),
            pagingSourceFactory = {
                EmployeeAttendanceRecordsPagingSource(attendanceDao, employeeId, startDate, endDate,holidays)
            })
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