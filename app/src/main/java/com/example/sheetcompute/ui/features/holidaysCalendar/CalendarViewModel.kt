package com.example.sheetcompute.ui.features.holidaysCalendar

import androidx.lifecycle.viewModelScope
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.data.repo.HolidayRepoInterface
import com.example.sheetcompute.domain.useCases.datetime.CalendarDayToDayOfWeekUseCase
import com.example.sheetcompute.domain.useCases.datetime.DayOfWeekToCalendarDayUseCase
import com.example.sheetcompute.domain.useCases.datetime.GenerateDateRangeUseCase
import com.example.sheetcompute.ui.features.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val holidayRepository: HolidayRepoInterface,
    private val preferencesDataSource: PreferencesGateway
) : BaseViewModel() {

    val weekendDays = preferencesDataSource.weekendDays
        .map { CalendarDayToDayOfWeekUseCase.execute(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    private val _holidaysByMonth = MutableStateFlow<MutableMap<YearMonth, List<Holiday>>>(hashMapOf())
    val holidaysByMonth: StateFlow<Map<YearMonth, List<Holiday>>> = _holidaysByMonth
        .map { it.toMap() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val holidaysEvents = holidaysByMonth.map { holidaysByMonth ->
        holidaysByMonth.flatMap { it.value }
            .flatMap { holiday -> GenerateDateRangeUseCase.execute(holiday.startDate, holiday.endDate) }
            .toSet()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    val holidaysForCurrentMonth: StateFlow<List<Holiday>> = combine(
        _currentMonth,
        _holidaysByMonth
    ) { currentMonth, holidaysMap ->
        holidaysMap[currentMonth] ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            preferencesDataSource.init()
            preloadMonths(_currentMonth.value, 5)
        }
    }

    suspend fun preloadMonths(centerMonth: YearMonth, range: Int) {
        val months = (-range..range).map { centerMonth.plusMonths(it.toLong()) }
        val newMonths = months.filter { !holidaysByMonth.value.keys.contains(it) }
        val holidaysMap = newMonths.associateWith { month -> loadHolidaysFor(month) }
        _holidaysByMonth.update { old -> old.toMutableMap().apply { putAll(holidaysMap) } }
    }

    fun loadHolidaysForMonth(month: YearMonth) {
        viewModelScope.launch {
            if (!_holidaysByMonth.value.containsKey(month)) {
                val holidays = loadHolidaysFor(month)
                _holidaysByMonth.update { old -> old.toMutableMap().apply { put(month, holidays) } }
            }
            _currentMonth.value = month
        }
    }

    private suspend fun loadHolidaysFor(month: YearMonth): List<Holiday> {
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()
        return holidayRepository.getHolidaysByDateRange(startDate, endDate)
    }

    fun addHoliday(holiday: Holiday) {
        viewModelScope.launch {
            holidayRepository.addHoliday(holiday)
            reloadCurrentMonth()
        }
    }

    fun updateHoliday(holiday: Holiday) {
        viewModelScope.launch {
            holidayRepository.updateHoliday(holiday)
            reloadCurrentMonth()
        }
    }

    fun deleteHoliday(holiday: Holiday) {
        viewModelScope.launch {
            holidayRepository.deleteHoliday(holiday)
            reloadCurrentMonth()
        }
    }

    private suspend fun reloadCurrentMonth() {
        val month = _currentMonth.value
        val refreshed = loadHolidaysFor(month)
        _holidaysByMonth.update { old -> old.toMutableMap().apply { put(month, refreshed) } }
    }

    fun updateWeekendDays(days: Set<DayOfWeek>) {
        viewModelScope.launch {
            val calendarDays = DayOfWeekToCalendarDayUseCase.execute(days)
            preferencesDataSource.setWeekendDays(calendarDays)
        }
    }

    fun updateCurrentMonth(month: YearMonth) {
        viewModelScope.launch {
            if (_currentMonth.value != month) {
                _currentMonth.value = month
                loadHolidaysForMonth(month)
            }
        }
    }
}