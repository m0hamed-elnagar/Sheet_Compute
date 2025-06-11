package com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory


import androidx.paging.PagingData
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.entities.DummyAttendanceData
import com.example.sheetcompute.ui.subFeatures.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar

class DateFilterViewModel : BaseViewModel() {
    // Date filters
    private val _selectedYear = MutableStateFlow<Int?>(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow<Int?>(null)

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    // Combined data flow
    val attendanceRecords: Flow<PagingData<AttendanceRecordUI>> = combine(
        _selectedYear,
        _selectedMonth
    ) { year, month ->
        getRecordsByMonth(year, month)
    }.flatMapLatest { it }

    private fun getRecordsByMonth(year: Int?, month: Int?): Flow<PagingData<AttendanceRecordUI>> {
        val filteredData = DummyAttendanceData.dummyRecords.filter { record ->
            (year == null || record.year == year) &&
                    (month == 0 || record.month == month ) // +1 because months are 1-12 in your data
        }
        _isEmpty.value = filteredData.isEmpty()
        return flowOf(PagingData.from(filteredData))
    }

    fun setSelectedYear(year: Int?) {
        _selectedYear.value = year
    }

    fun setSelectedMonth(month: Int?) {
        _selectedMonth.value = month
    }
}