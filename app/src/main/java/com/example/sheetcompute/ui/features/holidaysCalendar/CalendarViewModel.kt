package com.example.sheetcompute.ui.features.holidaysCalendar

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.domain.repo.HolidayRepositoryImpl
import com.example.sheetcompute.domain.useCases.datetime.*
import com.example.sheetcompute.ui.features.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import kotlin.collections.flatMap
import kotlin.collections.toSet

class CalendarViewModel(
) : BaseViewModel() {
    private val holidayRepository = HolidayRepositoryImpl()
    private val preferencesDataSource: PreferencesGateway = PreferencesGateway
    val weekendDays = preferencesDataSource.weekendDays.map {
        CalendarDayToDayOfWeekUseCase.execute(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    // Map of YearMonth to List<Holiday>
    private val _holidaysByMonth =
        MutableStateFlow<MutableMap<YearMonth, List<Holiday>>>(hashMapOf())
    val holidaysByMonth = _holidaysByMonth.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = hashMapOf()
    )
    val holidaysEvents = _holidaysByMonth.map { holidaysByMonth ->
        Log.e("khalid", "holidaysByMonth: $holidaysByMonth")
        // Flatten the map to a set of LocalDate representing all holidays
       holidaysByMonth.flatMap { it.value }
            .flatMap { holiday -> GenerateDateRangeUseCase.execute(holiday.startDate, holiday.endDate) }
            .toSet()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())
    private val _currentMonth = MutableStateFlow<YearMonth>(YearMonth.now())

    val currentMonth = _currentMonth.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = YearMonth.now()
    )
    val holidaysForCurrentMonth: StateFlow<List<Holiday>> = combine(
        _currentMonth,
        _holidaysByMonth
    ) { currentMonth, holidaysMap ->
        holidaysMap[currentMonth] ?: emptyList()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )


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
        val holidaysMap = newMonths.associateWith { month ->
            loadHolidaysFor(month)
        }
        _holidaysByMonth.value = _holidaysByMonth.value.toMutableMap().apply {
            putAll(holidaysMap)
        }
    }



    fun loadHolidaysForMonth(month: YearMonth) {
        viewModelScope.launch {
            if (!_holidaysByMonth.value.keys.contains(month)) {
                val holidays = loadHolidaysFor(month)
                _holidaysByMonth.value = _holidaysByMonth.value.toMutableMap().apply { put(month, holidays) }
            }
            _currentMonth.value = month
        }
    }

    private suspend fun loadHolidaysFor(month: YearMonth): List<Holiday> {
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()
        val holidays = holidayRepository.getHolidaysByDateRange(startDate, endDate)
        return holidays
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
        _holidaysByMonth.value = _holidaysByMonth.value.toMutableMap().apply {
            put(month, refreshed)
        }
    }
    fun updateWeekendDays(days: Set<DayOfWeek>) {
      viewModelScope.launch {
        val calendarDays = DayOfWeekToCalendarDayUseCase.execute(days)
        preferencesDataSource.setWeekendDays(calendarDays)
    }
    }

    // Kotlin
    fun updateCurrentMonth(month: YearMonth) {
        viewModelScope.launch(Dispatchers.Main) {
            if (_currentMonth.value != month) {
                _currentMonth.value = month
                loadHolidaysForMonth(month)
                Log.d("current", "updateCurrentMonth: ${_currentMonth.value}")
            }
        }
    }
}


