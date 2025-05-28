package com.example.sheetcompute.ui.features.holidaysCalendar

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.data.local.entities.Holiday
import com.example.sheetcompute.domain.repo.HolidayRepository
import com.example.sheetcompute.domain.repo.HolidayRepositoryImpl
import com.example.sheetcompute.ui.subFeatures.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth

class CalendarViewModel(
) : BaseViewModel() {
private val holidayRepository = HolidayRepositoryImpl()
    private val preferencesDataSource: PreferencesGateway = PreferencesGateway
    private val _holidays = MutableStateFlow(emptyList<Holiday>())
    val holidays: StateFlow<List<Holiday>> = _holidays

    private val _weekendDays = MutableStateFlow(emptySet<DayOfWeek>())
    val weekendDays: StateFlow<Set<DayOfWeek>> = _weekendDays

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load weekend days
            _weekendDays.value = preferencesDataSource.getWeekendDays() ?: setOf(DayOfWeek.FRIDAY)

            // Load current month holidays
            loadHolidaysForMonth(_currentMonth.value)
        }
    }

    fun loadHolidaysForMonth(month: YearMonth) {
        viewModelScope.launch {
            val startDate = month.atDay(1)
            val endDate = month.atEndOfMonth()
            _holidays.value = holidayRepository.getHolidaysByDateRange(startDate, endDate)
            _currentMonth.value = month
        }
    }

    fun addHoliday(holiday: Holiday) {
        viewModelScope.launch {
            holidayRepository.addHoliday(holiday)
            loadHolidaysForMonth(_currentMonth.value)
        }
    }

    fun updateHoliday(holiday: Holiday) {
        viewModelScope.launch {
            holidayRepository.updateHoliday(holiday)
            loadHolidaysForMonth(_currentMonth.value)
        }
    }

    fun deleteHoliday(holiday: Holiday) {
        viewModelScope.launch {
            holidayRepository.deleteHoliday(holiday)
            loadHolidaysForMonth(_currentMonth.value)
        }
    }

    fun updateWeekendDays(days: Set<DayOfWeek>) {
        viewModelScope.launch {
            _weekendDays.value = days
            preferencesDataSource.setWeekendDays(days)
        }
    }
}