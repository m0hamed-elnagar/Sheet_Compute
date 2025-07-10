package com.example.sheetcompute.ui.features.attendanceHistory.searchHistory

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.data.repo.EmployeeRepo
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.domain.excel.export.RejectionWorkbookBuilder
import com.example.sheetcompute.ui.features.base.BaseViewModel
import com.example.sheetcompute.ui.subFeatures.utils.ExcelFileSaver.saveToDownloads
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.InputStream

class SearchViewModel : BaseViewModel() {
    // Search query
    private val _searchQuery = MutableStateFlow("")
    private val employeeRepo = EmployeeRepo()
    private val attendanceRepo = AttendanceRepo()
    private val _refreshTrigger = MutableStateFlow(0)
    private val _employees = MutableStateFlow<List<EmployeeEntity>>(emptyList())
    val employees: StateFlow<List<EmployeeEntity>> = _employees.asStateFlow()
    init {
        observeRefreshTrigger()
    }

    private fun observeRefreshTrigger() {
        viewModelScope.launch {
            _refreshTrigger.collect {
                getAllEmployees()
            }
        }
    }

    fun refreshData() {
        _refreshTrigger.value++
        Log.d("SearchViewModel", "Data refresh triggered")
    }

    fun getAllEmployees(){
        viewModelScope.launch {
            _employees.value = employeeRepo.getAllEmployees()
        }
    }

    fun importDataFromExcel(
        inputStream: InputStream,
        onComplete: (String, ExcelImporter.ImportResult?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = ExcelImporter.import(
                    inputStream,
                    PreferencesGateway.getWorkStartTime(),
                    employeeRepo,
                    attendanceRepo
                )
                val message = "Imported: ${result.recordsAdded} records and ${result.newEmployees} new employees"
                Log.d("SearchViewModel", message)
                refreshData() // Trigger data refresh
                onComplete(message, result)
            } catch (e: Exception) {
                val errorMessage = "Failed to import data: ${e.message}"
                Log.e("SearchViewModel", errorMessage, e)
                onError(errorMessage)
            }
        }
    }

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    val attendanceRecords: Flow<List<EmployeeEntity>> =
        _searchQuery
            .flatMapLatest { query ->
                employees.map { list ->
                    list.filter { employee ->
                        query.isBlank() ||
                                employee.name.contains(query, ignoreCase = true) ||
                                employee.id.toString().contains(query, ignoreCase = true)
                    }.also { filtered ->
                        _isEmpty.value = filtered.isEmpty()
                    }
                }
            }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}