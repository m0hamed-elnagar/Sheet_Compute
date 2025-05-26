package com.example.sheetcompute.ui.features.holidaysCalendar

import androidx.lifecycle.viewModelScope
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.local.entities.Holiday
import com.example.sheetcompute.ui.subFeatures.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek

class CalendarViewModel  : BaseViewModel() {
    private val _holidays = MutableStateFlow(emptyList<Holiday>())
    val holidays: StateFlow<List<Holiday>> = _holidays

    private val _weekendDays = MutableStateFlow(PreferencesGateway.getWeekendDays() ?: setOf(DayOfWeek.FRIDAY))
    val weekendDays: StateFlow<Set<DayOfWeek>> = _weekendDays

    init {
        // Load weekend days from SharedPreferences when the ViewModel is initialized
        viewModelScope.launch {
            _weekendDays.value = PreferencesGateway.getWeekendDays() ?: setOf()
        }
    }
    fun updateWeekendDays(days: Set<DayOfWeek>) {
        viewModelScope.launch {
            _weekendDays.value = days
            // Save the updated weekend days to SharedPreferences
            PreferencesGateway.setWeekendDays(days)
        }
    }
}