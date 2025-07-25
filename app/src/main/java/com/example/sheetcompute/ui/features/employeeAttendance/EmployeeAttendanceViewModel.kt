package com.example.sheetcompute.ui.features.employeeAttendance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.ui.features.base.BaseViewModel
import kotlinx.coroutines.flow.*
import androidx.lifecycle.asLiveData
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.EmployeeRepo
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.domain.useCases.createCustomMonthRange
import com.example.sheetcompute.domain.usecase.GetEmployeeAttendanceRecordsUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.plus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeAttendanceViewModel @Inject constructor(
    private val getEmployeeAttendanceRecordsUseCase : GetEmployeeAttendanceRecordsUseCase,
    private val employeeRepo: EmployeeRepo,
    private val preferencesGateway: PreferencesGateway
) : BaseViewModel() {
    private val _dateRange = MutableStateFlow<ClosedRange<LocalDate>?>(null)
    private val _selectedStatuses = MutableStateFlow<Set<AttendanceStatus>>(emptySet())
    private val _cachedRecords = MutableStateFlow<List<EmployeeAttendanceRecord>>(emptyList())
    // Counters
    private val _presentCount = MutableStateFlow(0)
    private val _absentCount = MutableStateFlow(0)
    private val _extraDaysCount = MutableStateFlow(0)
    private val _tardiesCount = MutableStateFlow(0L)
    private var cachedEmployeeId: Long? = null

    private val _selectedEmployee = MutableStateFlow<EmployeeEntity?>(null)

    val presentCount: LiveData<Int> = _presentCount.asLiveData()
    val absentCount: LiveData<Int> = _absentCount.asLiveData()
    val extraDaysCount: LiveData<Int> = _extraDaysCount.asLiveData()
    val tardiesCount: LiveData<Long> = _tardiesCount.asLiveData()
    val isEmpty: LiveData<Boolean> = _cachedRecords.map { it.isEmpty() }.asLiveData()
    val selectedEmployee: StateFlow<EmployeeEntity?> = _selectedEmployee.asStateFlow()

    val filteredRecords: StateFlow<List<EmployeeAttendanceRecord>> =
        _cachedRecords.combine(_selectedStatuses) { records, statuses ->
      if (statuses.isEmpty()) records else records.filter { record ->
                statuses.any { status ->
                    if (status == AttendanceStatus.PRESENT) record.status != AttendanceStatus.ABSENT else record.status == status
                }
            }        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setEmployeeId(id: Long) {
        cachedEmployeeId = id
        fetchEmployeeById(id)
        tryInitialFetch()
    }
init {
    val now = LocalDate.now()
    setMonthRange(now.monthValue, now.year)
}
    fun setMonthRange(month: Int, year: Int) {
        Log.d("DateFilterHandler", "createCustomMonthRange: $month")
        val startDay = preferencesGateway.getMonthStartDay()
        _dateRange.value = createCustomMonthRange(month = month, year = year, startDay = startDay)
        tryInitialFetch()
    }

    fun setCustomRange(startDate: LocalDate, endDate: LocalDate) {
        _dateRange.value = startDate..endDate
        tryInitialFetch()
    }

    private fun tryInitialFetch() {
        val id = cachedEmployeeId
        val range = _dateRange.value
        if (id != null && range != null) {
            viewModelScope.launch {
                fetchRecordsForEmployee(range)
            }
        }
    }

    suspend fun fetchRecordsForEmployee(range: ClosedRange<LocalDate>?) {
        val employeeId = cachedEmployeeId ?: return
        val records = if (range != null) getRecordsByDateRange(employeeId, range) else emptyList()
        _cachedRecords.value = records
        updateCounters(records)
    }

    private suspend fun getRecordsByDateRange(employeeId: Long, range: ClosedRange<LocalDate>): List<EmployeeAttendanceRecord> {
        return getEmployeeAttendanceRecordsUseCase(employeeId, range.start, range.endInclusive)
    }

    private fun updateCounters(records: List<EmployeeAttendanceRecord>) {
        _presentCount.value = records.count { it.status != AttendanceStatus.ABSENT }
        _absentCount.value = records.count { it.status == AttendanceStatus.ABSENT }
        _extraDaysCount.value = records.count { it.status == AttendanceStatus.EXTRA_DAY }
        _tardiesCount.value = records
            .filter { it.status == AttendanceStatus.LATE }
            .sumOf { it.lateDuration  }
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

    fun fetchEmployeeById(id: Long) {
        viewModelScope.launch {
            val employee = employeeRepo.getEmployeeById(id)
            _selectedEmployee.value = employee
        }
    }
}