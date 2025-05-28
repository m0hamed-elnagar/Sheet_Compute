package com.example.sheetcompute.ui.features.employeeAttendance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.sheetcompute.data.local.entities.AttendanceStatus
import com.example.sheetcompute.data.local.entities.DummyAttendanceData2
import com.example.sheetcompute.data.local.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.ui.subFeatures.base.BaseViewModel
import kotlinx.coroutines.flow.*
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
            range?.contains(it.date) ?: true
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
        Log.d("DateFilterHandler", "create26to25Range: $month")

        _dateRange.value = create26to25Range(month, year)
    }

    fun setCustomRange(startDate: LocalDate, endDate: LocalDate) {
        _dateRange.value = startDate..endDate
    }

    private fun create26to25Range(month: Int, year: Int): ClosedRange<LocalDate>? {
        Log.d("DateFilterHandler", "create26to25Range: $month")
        return if (month == 0) {
            // Special case: all months selected
            val start = LocalDate.of(year - 1, 12, 26)
            val end = LocalDate.of(year, 12, 25)
            start..end
        } else {
            val start: LocalDate
            val end: LocalDate

            if (month == 1) {
                // January: previous year December 26 to current year January 25
                start = LocalDate.of(year - 1, 12, 26)
                end = LocalDate.of(year, 1, 25)
            } else {
                // All other months
                start = LocalDate.of(year, month - 1, 26)
                end = LocalDate.of(year, month, 25)
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