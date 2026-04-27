package com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory


import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.domain.useCases.attendance.GetAttendanceSummaryPagerUseCase
import com.example.sheetcompute.domain.useCases.attendance.GetAvailableMonthsUseCase
import com.example.sheetcompute.domain.useCases.createCustomMonthRange
import com.example.sheetcompute.ui.features.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import java.io.InputStream
import java.util.Calendar
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
@VisibleForTesting
internal class DateFilterViewModel @Inject constructor(
    private val excelImporter: ExcelImporter,
    private val preferencesGateway: PreferencesGateway,
    private val getAttendanceSummaryPagerUseCase: GetAttendanceSummaryPagerUseCase,
    private val getAvailableMonthsUseCase: GetAvailableMonthsUseCase
) : BaseViewModel() {
    // Date filters
    private val _selectedYear = MutableStateFlow<Int?>(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow<Int?>(null)
val selectedYear: StateFlow<Int?> = _selectedYear
    val selectedMonth: StateFlow<Int?> = _selectedMonth

    private val _availableMonthStrings = MutableStateFlow<List<String>>(emptyList())
    
    val availableYears: StateFlow<List<Int>> = _availableMonthStrings.map { strings ->
        strings.mapNotNull { it.split("-").firstOrNull()?.toIntOrNull() }.distinct().sortedDescending()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val availableMonthsForSelectedYear: Flow<List<Int>> = combine(_availableMonthStrings, _selectedYear) { strings, year ->
        if (year == null) emptyList()
        else strings.filter { it.startsWith("$year-") }
            .mapNotNull { it.split("-").getOrNull(1)?.toIntOrNull() }
            .map { it - 1 } // Convert to 0-based
            .distinct().sorted()
    }

    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger

    init {
        fetchAvailableDates()
    }

    private fun fetchAvailableDates() {
        viewModelScope.launch {
            _availableMonthStrings.value = getAvailableMonthsUseCase()
            // Set initial selection to latest available if current is not available
            if (_selectedYear.value == null && availableYears.value.isNotEmpty()) {
                _selectedYear.value = availableYears.value.first()
            }
        }
    }

    val attendanceRecords: Flow<PagingData<AttendanceRecordUI>> = combine(
        _selectedYear,
        _selectedMonth,
        _refreshTrigger
    ) { year, month, forceRefresh ->
        if (year != null) {
            val monthToUse = month ?: 0
            val range = createCustomMonthRange(monthToUse, year, preferencesGateway.getMonthStartDay())
            if (range != null) {
                // Create new pager
                getAttendanceSummaryPagerUseCase(monthToUse, year, range, 20)
            } else {
                null
            }
        } else {
            null
        }
    }.flatMapLatest { pager ->
        pager?.flow ?: flowOf(PagingData.empty())
    }.cachedIn(viewModelScope)



    fun setSelectedYear(year: Int?) {
        _selectedYear.value = year
        // Force recreation of pager

    }

    fun setSelectedMonth(month: Int?) {
        if (month == null || month in 0..11) {
            _selectedMonth.value = month

        }
        // else ignore invalid month
    }

    fun refreshData() {
        _refreshTrigger.value++
    }

    fun importDataFromExcel(
        inputStream: InputStream,
        onComplete: (String, ExcelImporter.ImportResult?) -> Unit,
        onError: (String) -> Unit
    ) {        viewModelScope.launch {
            try {
                val result = excelImporter.import(
                    inputStream,
                )
                val message = "Imported: ${result.recordsAdded} records and ${result.newEmployees} new employees"
                refreshData() // Trigger data refresh
                fetchAvailableDates() // Refetch available dates after import
                onComplete(message, result)
            } catch (e: Exception) {
                val errorMessage = "Failed to import data: ${e.message}"
                onError(errorMessage)
            }
        }
    }
}