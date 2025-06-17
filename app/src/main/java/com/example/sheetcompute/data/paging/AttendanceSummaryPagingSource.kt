package com.example.sheetcompute.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sheetcompute.data.daos.AttendanceDao
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import java.time.LocalDate

class AttendanceSummaryPagingSource(
    private val dao: AttendanceDao,
    private val month: Int,
    private val year: Int,
    private val range: ClosedRange<LocalDate>,
    private val pageSize: Int
) : PagingSource<Int, AttendanceRecordUI>() {

    override fun getRefreshKey(state: PagingState<Int, AttendanceRecordUI>): Int? = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendanceRecordUI> {
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
            val workingDays = countWorkingDays(range)

            val data = rawList.map {
                mapToUI(it, workingDays)
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
