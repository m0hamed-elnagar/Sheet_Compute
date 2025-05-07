package com.example.sheetcompute.ui.attendanceHistory

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.sheetcompute.data.roomDB.entities.AttendanceRecordUI
import com.example.sheetcompute.data.roomDB.entities.DummyAttendanceData
import com.example.sheetcompute.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar

enum class ViewState {
    MONTH,
    SEARCH
}

class AttendanceViewModel : BaseViewModel() {
    // View state management
    private val _currentViewState = MutableStateFlow(ViewState.SEARCH)
    val currentViewState: StateFlow<ViewState> = _currentViewState.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    // Date filters
    private val _selectedYear = MutableStateFlow<Int?>(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow<Int?>(null)

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    // Combined data flow
    val attendanceRecords: Flow<PagingData<AttendanceRecordUI>> = combine(
        _currentViewState,
        _searchQuery,
        _selectedYear,
        _selectedMonth
    ) { viewState, query, year, month ->
        Quad(viewState, query, year, month)
    }.flatMapLatest { (viewState, query, year, month) ->
        when (viewState) {
            ViewState.MONTH -> getRecordsByMonth(year, month)
            ViewState.SEARCH -> getRecordsBySearch(query)
        }
    }.cachedIn(viewModelScope)

    private fun getRecordsByMonth(year: Int?, month: Int?): Flow<PagingData<AttendanceRecordUI>> {
        _isLoading.value = true
        return try {
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

    private fun getRecordsBySearch(query: String): Flow<PagingData<AttendanceRecordUI>> {
        _isLoading.value = true
        return try {
            val filteredData = DummyAttendanceData.dummyRecords.filter { record ->
                query.isBlank() ||
                        record.name.contains(query, ignoreCase = true) ||
                        record.id.toString().contains(query, ignoreCase = true) ||
                        record.month.toString().contains(query, ignoreCase = true)
            }
            _isEmpty.value = filteredData.isEmpty()
            flowOf(PagingData.from(filteredData))
        } finally {
            _isLoading.value = false
        }
    }

    fun switchToMonthView() {
        _currentViewState.value = ViewState.MONTH
        _searchQuery.value = ""
    }

    fun switchToSearchView() {
        _currentViewState.value = ViewState.SEARCH
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedYear(year: Int?) {
        _selectedYear.value = year
    }

    fun setSelectedMonth(month: Int?) {
        _selectedMonth.value = month
    }

    fun refresh() {
        // Trigger refresh by updating the states
        when (_currentViewState.value) {
            ViewState.MONTH -> {
                val currentYear = _selectedYear.value
                _selectedYear.value = null
                _selectedYear.value = currentYear
            }

            ViewState.SEARCH -> {
                val currentQuery = _searchQuery.value
                _searchQuery.value = ""
                _searchQuery.value = currentQuery
            }
        }
    }

    // Helper data class for combine
    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}