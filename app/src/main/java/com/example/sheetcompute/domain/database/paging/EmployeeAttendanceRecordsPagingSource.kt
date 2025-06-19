package com.example.sheetcompute.domain.database.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sheetcompute.domain.database.daos.AttendanceDao
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import java.time.LocalDate

class EmployeeAttendanceRecordsPagingSource(
    private val attendanceDao: AttendanceDao,
    private val employeeId: Long,
    private val startDate: LocalDate,
    private val endDate: LocalDate,
    private val holidays : List<LocalDate> = emptyList()
) : PagingSource<Int, AttendanceRecord>() {
    override fun getRefreshKey(state: PagingState<Int, AttendanceRecord>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendanceRecord> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            val records = attendanceDao.getEmployeeRecordsByDateRangePaged(
                employeeId = employeeId, startDate = startDate,
                endDate = endDate, pageSize, offset
            )
            records.forEach { it.toEmployeeAttendanceRecord(holidays) }
            LoadResult.Page(
                data = records,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (records.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
fun AttendanceRecord.toEmployeeAttendanceRecord(holidays: List<LocalDate>): EmployeeAttendanceRecord {
    val status = when {
        date in holidays -> AttendanceStatus.EXTRA_DAY
        tardyMinutes >= 1 -> AttendanceStatus.LATE
        else ->AttendanceStatus.PRESENT
    }

    return EmployeeAttendanceRecord(
        id =id,
        employeeId = employeeId,
        loginTime = clockIn,
        date = date,
        lateDuration = tardyMinutes,
        status = status
    )
}
