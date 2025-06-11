package com.example.sheetcompute.ui.features.employeeAttendance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.DummyAttendanceData2
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.ui.subFeatures.base.BaseViewModel
import kotlinx.coroutines.flow.*
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.plus

class EmployeeAttendanceViewModel : BaseViewModel() {
    private val _dateRange = MutableStateFlow<ClosedRange<LocalDate>?>(null)
    private val _selectedStatuses = MutableStateFlow<Set<AttendanceStatus>>(emptySet())
    private val _cachedRecords = MutableStateFlow<List<EmployeeAttendanceRecord>>(emptyList())

    // Counters
    private val _presentCount = MutableStateFlow(0)
    private val _absentCount = MutableStateFlow(0)
    private val _extraDaysCount = MutableStateFlow(0)
    private val _tardiesCount = MutableStateFlow(0L)

    val presentCount: LiveData<Int> = _presentCount.asLiveData()
    val absentCount: LiveData<Int> = _absentCount.asLiveData()
    val extraDaysCount: LiveData<Int> = _extraDaysCount.asLiveData()
    val tardiesCount: LiveData<Long> = _tardiesCount.asLiveData()
    val isEmpty: LiveData<Boolean> = _cachedRecords.map { it.isEmpty() }.asLiveData()

    val filteredRecords: Flow<PagingData<EmployeeAttendanceRecord>> =
        _cachedRecords.combine(_selectedStatuses) { records, statuses ->
            PagingData.from(if (statuses.isEmpty()) records else records.filter { it.status in statuses })
        }

    init {
        viewModelScope.launch {
            _dateRange.collect { range ->
                val records = getRecordsByDateRange(range)
                _cachedRecords.value = records
                updateCounters(records)
            }
        }
    }

    private fun getRecordsByDateRange(range: ClosedRange<LocalDate>?): List<EmployeeAttendanceRecord> {
        return DummyAttendanceData2.employeeAttendanceRecords.filter {
            range?.contains(it.date) != false
        }.sortedBy { it.date }
    }

    private fun updateCounters(records: List<EmployeeAttendanceRecord>) {
        _presentCount.value = records.count { it.status == AttendanceStatus.PRESENT }
        _absentCount.value = records.count { it.status == AttendanceStatus.ABSENT }
        _extraDaysCount.value = records.count { it.status == AttendanceStatus.EXTRA_DAY }
        _tardiesCount.value = records
            .filter { it.status == AttendanceStatus.LATE }
            .sumOf { it.lateDuration / 60 }
    }

    // Date range controls
    fun setMonthRange(month: Int, year: Int) {
        Log.d("DateFilterHandler", "createCustomMonthRange: $month")

        _dateRange.value = createCustomMonthRange(month, year)
    }

    fun setCustomRange(startDate: LocalDate, endDate: LocalDate) {
        _dateRange.value = startDate..endDate
    }

    private fun createCustomMonthRange(month: Int, year: Int): ClosedRange<LocalDate>? {
        val startDay = PreferencesGateway.getMonthStartDay()
        val endDay = if (startDay == 1) LocalDate.of(year, month, 1).lengthOfMonth() else startDay - 1
        return if (month == 0) {
            // Special case: all months selected
            val start = LocalDate.of(year - 1, 12, startDay)
            val end = LocalDate.of(year, 12, endDay)
            start..end
        } else {
            val start: LocalDate
            val end: LocalDate

            if (month == 1) {
                // January: previous year December startDay to current year January endDay
                start = LocalDate.of(year - 1, 12, startDay)
                end = LocalDate.of(year, 1, endDay)
            } else {
                // All other months
                start = LocalDate.of(year, month - 1, startDay)
                end = LocalDate.of(year, month, endDay)
            }
            start..end
        }
    }

    // Status filter controls
    fun toggleStatusFilter(status: AttendanceStatus) {
        _selectedStatuses.update { current ->
            if (current.contains(status)) current - status else current + status
        }
    }

    fun isStatusSelected(status: AttendanceStatus): Boolean {
        return _selectedStatuses.value.contains(status)
    }

    fun clearFilters() {
        _dateRange.value = null
        _selectedStatuses.value = emptySet()
    }
}