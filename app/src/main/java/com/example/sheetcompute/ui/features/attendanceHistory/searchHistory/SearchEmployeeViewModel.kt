package com.example.sheetcompute.ui.features.attendanceHistory.searchHistory

import androidx.lifecycle.viewModelScope
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.repo.EmployeeRepo
import com.example.sheetcompute.ui.features.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel : BaseViewModel() {
    // Search query
    private val _searchQuery = MutableStateFlow("")
    private val repository = EmployeeRepo()
    private val _employees = MutableStateFlow<List<EmployeeEntity>>(emptyList())
    val employees: StateFlow<List<EmployeeEntity>> = _employees.asStateFlow()
    init {
        getAllEmployees()
    }

    fun getAllEmployees(){
        viewModelScope.launch {
            _employees.value=    repository.getAllEmployees()
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