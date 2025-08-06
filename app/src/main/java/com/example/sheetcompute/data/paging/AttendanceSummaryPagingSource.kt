package com.example.sheetcompute.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.local.room.daos.AttendanceDao
import com.example.sheetcompute.data.mappers.mapToAttendanceRecordUI
import java.time.LocalDate

class AttendanceSummaryPagingSource(
    private val dao: AttendanceDao,
    private val month: Int,
    private val year: Int,
    private val range: ClosedRange<LocalDate>,
    private val totalWorkingDays: Int,
    private val pageSize: Int,
) : PagingSource<Int, AttendanceRecordUI>() {

//    override fun getRefreshKey(state: PagingState<Int, AttendanceRecordUI>): Int? = 0
    override fun getRefreshKey(state: PagingState<Int, AttendanceRecordUI>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendanceRecordUI> {
        // Wait for invalidation signal if present

        val page = params.key ?: 0
        val offset = page * pageSize

        return try {
            val rawList = dao.getAttendanceSummary(
                startDate = range.start,
                endDate = range.endInclusive,
                month = month,
                year = year,
                limit = pageSize,
                offset = offset
            )

            val data = rawList
                .filter { it.presentDays > 0 }
                .map {
                    it.mapToAttendanceRecordUI(totalWorkingDays)
                }

            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }



}
