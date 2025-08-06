package com.example.sheetcompute.testUtils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sheetcompute.data.entities.AttendanceRecordUI

/**
 * A simple PagingSource for testing that returns the provided list as a single page.
 */


class MockPagingSource(
    private val items: List<AttendanceRecordUI>
) : PagingSource<Int, AttendanceRecordUI>() {
    override fun getRefreshKey(state: PagingState<Int, AttendanceRecordUI>) = 0
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendanceRecordUI> {
        return LoadResult.Page(
            data = items,
            prevKey = null,
            nextKey = null
        )
    }
}
