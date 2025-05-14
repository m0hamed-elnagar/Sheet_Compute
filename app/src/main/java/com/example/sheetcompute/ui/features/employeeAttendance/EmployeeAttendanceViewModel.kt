package com.example.sheetcompute.ui.features.employeeAttendance

import androidx.paging.PagingData
import com.example.sheetcompute.data.roomDB.entities.AttendanceStatus
import com.example.sheetcompute.data.roomDB.entities.DummyAttendanceData2
import com.example.sheetcompute.data.roomDB.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.ui.subFeatures.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class EmployeeAttendanceViewModel : BaseViewModel() {
    // Date filters
    private val _selectedYear = MutableStateFlow<Int?>(null)
    private val _selectedMonth = MutableStateFlow<Int?>(null)

    // Status filters
    private val _filterByStatus = MutableStateFlow<AttendanceStatus?>(null)

    // Counters
    private val _presentCount = MutableStateFlow(0)
    private val _absentCount = MutableStateFlow(0)
    private val _extraDaysCount = MutableStateFlow(0)
    private val _tardiesCount = MutableStateFlow(0L)

    val presentCount: StateFlow<Int> = _presentCount.asStateFlow()
    val absentCount: StateFlow<Int> = _absentCount.asStateFlow()
    val extraDaysCount: StateFlow<Int> = _extraDaysCount.asStateFlow()
    val tardiesCount: StateFlow<Long> = _tardiesCount.asStateFlow()

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    // Make filteredRecords public
    val filteredRecords: Flow<PagingData<EmployeeAttendanceRecord>> = combine(
        _selectedYear,
        _selectedMonth,
        _filterByStatus
    ) { year, month, status ->
        Triple(year, month, status)
    }.flatMapLatest { (year, month, status) ->
        flow {
            _loading.value = true
            val filteredData = DummyAttendanceData2.employeeAttendanceRecords.filter { record ->
                val recordYear = record.date.year
                val recordMonth = record.date.monthValue
                (year == null || recordYear == year) &&
                        (month == null || recordMonth == month + 1) &&
                        (status == null || record.status == status)
            }
            _isEmpty.value = filteredData.isEmpty()
            emit(PagingData.from(filteredData))
            _loading.value = false
        }
    }

    fun setSelectedYear(year: Int?) {
        _selectedYear.value = year
    }

    fun setSelectedMonth(month: Int?) {
        _selectedMonth.value = month
    }

    fun setFilterByStatus(status: AttendanceStatus?) {
        _filterByStatus.value = status
    }

    init {
        calculateCounters()
    }

    private fun calculateCounters() {
        val records = DummyAttendanceData2.employeeAttendanceRecords
        _presentCount.value = records.count { it.status == AttendanceStatus.PRESENT }
        _absentCount.value = records.count { it.status == AttendanceStatus.ABSENT }
        _extraDaysCount.value = records.count { it.status == AttendanceStatus.EXTRA_DAY }

        val totalLateHours = records
            .filter { it.status == AttendanceStatus.LATE }
            .sumOf {
               it.lateDuration/ 60 // Convert minutes to hours
            }
        _tardiesCount.value = totalLateHours
    }
}