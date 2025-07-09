package com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory


import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.data.repo.HolidayRepo
import com.example.sheetcompute.domain.useCases.createCustomMonthRange
import com.example.sheetcompute.ui.features.base.BaseViewModel
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.data.repo.EmployeeRepo
import com.example.sheetcompute.domain.useCases.attendance.GetAttendanceSummaryPagerUseCase
import com.example.sheetcompute.domain.useCases.workingDays.CountWorkingDaysUseCase
import java.io.InputStream

class DateFilterViewModel : BaseViewModel() {
    // Date filters
    private val _selectedYear = MutableStateFlow<Int?>(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow<Int?>(null)

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty
    private val employeeRepo by lazy { EmployeeRepo() }
    private val attendanceRepo = AttendanceRepo()
    private val holidayRepo = HolidayRepo()
    private val calculateWorkingDaysUseCase = CountWorkingDaysUseCase(holidayRepo)
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
        Log.d("DateFilterViewModel", "Data refresh triggered")
    }

    fun importDataFromExcel(inputStream: InputStream, onComplete: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = ExcelImporter.import(
                    inputStream,
                    PreferencesGateway.getWorkStartTime(),
                    employeeRepo,
                    attendanceRepo
                )
                val message = "Imported: ${result.recordsAdded} records and ${result.newEmployees} new employees"
                Log.d("DateFilterViewModel", message)
                refreshData() // Trigger data refresh
                onComplete(message)
                refreshData()
            } catch (e: Exception) {
                val errorMessage = "Failed to import data: ${e.message}"
                Log.e("DateFilterViewModel", errorMessage, e)
                onError(errorMessage)
            }
        }
    }
}