package com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory


import androidx.lifecycle.viewModelScope
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
import java.time.LocalDate
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DateFilterViewModel : BaseViewModel() {
    // Date filters
    private val _selectedYear = MutableStateFlow<Int?>(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow<Int?>(null)

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    private val attendanceRepo = AttendanceRepo()
    private val holidayRepo = HolidayRepo()
    private val calculateWorkingDaysUseCase = CalculateWorkingDaysUseCase(holidayRepo)
    private val getAttendanceSummaryPagerUseCase = GetAttendanceSummaryPagerUseCase(attendanceRepo, calculateWorkingDaysUseCase)

    private val _currentMonthWorkingDays = MutableStateFlow<Int?>(null)
    val currentMonthWorkingDays: StateFlow<Int?> = _currentMonthWorkingDays.asStateFlow()

    // Combined data flow
    val attendanceRecords: Flow<PagingData<AttendanceRecordUI>> = combine(_selectedYear, _selectedMonth) { year, month ->
        if (year != null && month != null) {
            val pageSize = 20 // or any default page size you want
            val startDay = PreferencesGateway.getMonthStartDay()
            val range = createCustomMonthRange(month+1, year, startDay)
            if (range != null) {
                // Calculate working days and update state
                viewModelScope.launch {
                    val workingDays = calculateWorkingDaysUseCase(range.start, range.endInclusive)
                    _currentMonthWorkingDays.value = workingDays
                }
                getAttendanceSummaryPagerUseCase(month, year, range, pageSize)
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
            combine(_selectedYear, _selectedMonth) { year, month ->
                year to month
            }.collect { (year, month) ->
                if (year != null && month != null) {
                    val startDay = PreferencesGateway.getMonthStartDay()
                    val range = createCustomMonthRange(month+1, year, startDay)
                    if (range != null) {
                        val workingDays = calculateWorkingDaysUseCase(range.start, range.endInclusive)
                        _currentMonthWorkingDays.value = workingDays
                    } else {
                        _currentMonthWorkingDays.value = null
                    }
                } else {
                    _currentMonthWorkingDays.value = null
                }
            }
        }
    }


    fun setSelectedYear(year: Int?) {
        _selectedYear.value = year
    }

    fun setSelectedMonth(month: Int?) {
        _selectedMonth.value = month
    }
}