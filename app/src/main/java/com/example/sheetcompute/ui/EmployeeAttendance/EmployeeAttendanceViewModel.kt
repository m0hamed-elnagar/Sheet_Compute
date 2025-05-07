package com.example.sheetcompute.ui.EmployeeAttendance

import androidx.paging.PagingData
import com.example.sheetcompute.data.roomDB.entities.AttendanceRecordUI
import com.example.sheetcompute.data.roomDB.entities.DummyAttendanceData
import com.example.sheetcompute.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar

class EmployeeAttendanceViewModel : BaseViewModel() {
    // Date filters
    private val _selectedYear = MutableStateFlow<Int?>(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow<Int?>(null)

    //todo put the loading in the baseViewModel
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    private fun getRecordsByMonth(year: Int?, month: Int?): Flow<PagingData<AttendanceRecordUI>> {
        _isLoading.value = true
        return try {
            //todo make dummyData for the EmployeeAttendance
            val filteredData = DummyAttendanceData.dummyRecords.filter { record ->
                (year == null || record.year == year) &&
                        (month == null || record.month == month + 1) // +1 because months are 1-12 in your data
            }
            _isEmpty.value = filteredData.isEmpty()
            flowOf(PagingData.from(filteredData))
        } finally {
            _isLoading.value = false
        }
    }

    fun setSelectedYear(year: Int?) {
        _selectedYear.value = year
    }

    fun setSelectedMonth(month: Int?) {
        _selectedMonth.value = month
    }
// this vm will filter by status and date and get the dummy data as paging data
}
