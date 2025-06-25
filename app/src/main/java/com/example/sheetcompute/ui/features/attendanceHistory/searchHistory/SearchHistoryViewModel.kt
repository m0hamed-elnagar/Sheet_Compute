package com.example.sheetcompute.ui.features.attendanceHistory.searchHistory

import androidx.paging.PagingData
import com.example.sheetcompute.entities.AttendanceRecordUI
import com.example.sheetcompute.entities.DummyAttendanceData
import com.example.sheetcompute.ui.features.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class SearchViewModel : BaseViewModel() {
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    val attendanceRecords: Flow<PagingData<AttendanceRecordUI>> =
        _searchQuery
            .flatMapLatest { query ->
                flow {
                    _loading.value = true
                    try {
                        val filteredData = DummyAttendanceData.dummyRecords.filter { record ->
                            query.isBlank() ||
                                    record.name.contains(query, ignoreCase = true) ||
                                    record.id.toString().contains(query, ignoreCase = true)
                        }
                        _isEmpty.value = filteredData.isEmpty()
                        emit(PagingData.from(filteredData))
                    } finally {
                        _loading.value = false
                    }
                }
            }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}