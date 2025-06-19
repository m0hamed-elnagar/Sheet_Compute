package com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory


import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.domain.repo.AttendanceRepo
import com.example.sheetcompute.domain.repo.HolidayRepo
import com.example.sheetcompute.domain.useCases.CalculateWorkingDaysUseCase
import com.example.sheetcompute.domain.useCases.GetAttendanceSummaryPagerUseCase
import com.example.sheetcompute.domain.useCases.createCustomMonthRange
import com.example.sheetcompute.ui.features.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DateFilterViewModel : BaseViewModel() {
    // Date filters
    private val _selectedYear = MutableStateFlow<Int?>(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow<Int?>(null)
    private val _currentMonthWorkingDays = MutableStateFlow<Int?>(null)

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty
    val currentMonthWorkingDays: StateFlow<Int?> = _currentMonthWorkingDays.asStateFlow()

    private val attendanceRepo = AttendanceRepo()
    private val holidayRepo = HolidayRepo()
    private val calculateWorkingDaysUseCase = CalculateWorkingDaysUseCase(holidayRepo)
    private val getAttendanceSummaryPagerUseCase =
        GetAttendanceSummaryPagerUseCase(attendanceRepo, calculateWorkingDaysUseCase)
    private val _refreshTrigger = MutableStateFlow(0)

    val attendanceRecords: Flow<PagingData<AttendanceRecordUI>> = combine(
        _selectedYear,
        _selectedMonth,
        _refreshTrigger
    ) { year, month, forceRefresh ->
        if (year != null && month != null) {
            val range = createCustomMonthRange(month , year, PreferencesGateway.getMonthStartDay())
            Log.d("DateFilterViewModel", "Selected range: $range")
            if (range != null) {
                // Calculate working days
                viewModelScope.launch {
                    _currentMonthWorkingDays.value = calculateWorkingDaysUseCase(range.start, range.endInclusive)
                }
                // Create new pager
                getAttendanceSummaryPagerUseCase(month, year, range, 20)
            } else {
                null
            }
        } else {
            null
        }
    }.flatMapLatest { pager ->
        pager?.flow ?: flowOf(PagingData.empty())
    }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            attendanceRecords.collect { pagingData ->
                _isEmpty.value = pagingData == PagingData.empty<AttendanceRecordUI>()
                Log.d("DateFilterViewModel", " isEmpty: ${_isEmpty.value}")
            }
        }
    }

    fun setSelectedYear(year: Int?) {
        _selectedYear.value = year
        Log.d("DateFilterViewModel", "Year changed to: $year")
        // Force recreation of pager

    }

    fun setSelectedMonth(month: Int?) {
        if (month == null || month in 0..11) {
            _selectedMonth.value = month
            Log.d("DateFilterViewModel", "Month changed to: $month")

        }
        // else ignore invalid month
    }

    fun refreshData() {
        _refreshTrigger.value++
    }
}