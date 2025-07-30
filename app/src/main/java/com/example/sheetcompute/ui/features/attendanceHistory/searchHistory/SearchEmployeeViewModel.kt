package com.example.sheetcompute.ui.features.attendanceHistory.searchHistory

import androidx.lifecycle.viewModelScope
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.repo.EmployeeRepo
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.ui.features.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.InputStream

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val employeeRepo: EmployeeRepo,
    private val excelImporter: ExcelImporter
) : BaseViewModel() {
    // Search query
    private val _searchQuery = MutableStateFlow("")
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
                val result = excelImporter.import(
                    inputStream
                )
                val message = "Imported: ${result.recordsAdded} records and ${result.newEmployees} new employees"

                refreshData() // Trigger data refresh
                onComplete(message, result)
            } catch (e: Exception) {
                val errorMessage = "Failed to import data: ${e.message}"

                onError(errorMessage)
            }
        }
    }

    // Empty state
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    val filteredEmployees: Flow<List<EmployeeEntity>> =
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