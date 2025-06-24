package com.example.sheetcompute.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sheetcompute.domain.gateways.database.daos.AttendanceDao
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.ui.subFeatures.utils.filter
import java.time.LocalDate

class EmployeeAttendanceRecordsPagingSource(
    private val attendanceDao: AttendanceDao,
    private val employeeId: Long,
    private val startDate: LocalDate,
    private val endDate: LocalDate,
    private val holidays: List<LocalDate> = emptyList()
) : PagingSource<Int, EmployeeAttendanceRecord>() {

    private val allValidDates = (startDate..endDate).filter { it !in holidays }

    override fun getRefreshKey(state: PagingState<Int, EmployeeAttendanceRecord>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EmployeeAttendanceRecord> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            val pageDates = allValidDates.drop(offset).take(pageSize)

            val recordsFromDb = attendanceDao.getEmployeeRecordsByDateList(
                employeeId = employeeId,
                dates= pageDates
            )

            val recordMap = recordsFromDb.associateBy { it.date }

            val finalRecords: List<EmployeeAttendanceRecord> = pageDates.map { date ->
                val record = recordMap[date]
                record?.toEmployeeAttendanceRecord(holidays)
                    ?: // No record in DB = Absent
                    EmployeeAttendanceRecord(
                        id = date.toEpochDay().toLong() * -1, // unique fake ID
                        employeeId = employeeId,
                        loginTime = "",
                        date = date,
                        lateDuration = 0,
                        status = AttendanceStatus.ABSENT
                    )
            }

            LoadResult.Page(
                data = finalRecords,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (finalRecords.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
fun AttendanceRecord.toEmployeeAttendanceRecord(holidays: List<LocalDate>): EmployeeAttendanceRecord {
    val status = when {
        date in holidays -> AttendanceStatus.EXTRA_DAY
        tardyMinutes > 0 -> AttendanceStatus.LATE
        else -> AttendanceStatus.PRESENT
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
