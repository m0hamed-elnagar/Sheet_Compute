package com.example.sheetcompute.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sheetcompute.data.daos.EmployeeAttendanceDao
import com.example.sheetcompute.data.entities.AttendanceRecord
import java.util.Date

class EmployeeAttendanceRecordsPagingSource (private val attendanceDao: EmployeeAttendanceDao,
                                             private val employeeId: String,
                           private val startDate: Date,
                           private val endDate: Date) : PagingSource<Int, AttendanceRecord>() {
    override fun getRefreshKey(state: PagingState<Int, AttendanceRecord>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendanceRecord> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            val records = attendanceDao.getEmployeeRecordsByDateRangePaged(employeeId= employeeId, startDate = startDate,
                endDate = endDate,pageSize, offset)
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