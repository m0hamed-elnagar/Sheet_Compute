package com.example.sheetcompute.ui.features.settingFragment

import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.ui.features.base.BaseViewModel
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewmodel : BaseViewModel()  {
    private val preferencesDataSource: PreferencesGateway = PreferencesGateway

    private val _workStartTime = MutableStateFlow(preferencesDataSource.getWorkStartTime())
    val workStartTime: StateFlow<LocalTime> = _workStartTime.asStateFlow()

    private val _monthStartDay = MutableStateFlow(preferencesDataSource.getMonthStartDay())
    val monthStartDay: StateFlow<Int> = _monthStartDay.asStateFlow()

    fun setWorkStartTime(time: LocalTime) {
        _workStartTime.value = time
    }

    fun setMonthStartDay(day: Int) {
        _monthStartDay.value = day
    }
    fun saveSettings() {
        preferencesDataSource.saveWorkStartTime(_workStartTime.value)
        preferencesDataSource.setMonthStartDay(_monthStartDay.value)
    }
}