package com.example.sheetcompute.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.sheetcompute.entities.DEFAULT_MONTH_START_DAY
import com.example.sheetcompute.entities.KEY_WORK_START_TIME
import com.example.sheetcompute.entities.MONTH_START_DAY_KEY
import com.example.sheetcompute.entities.SHARED_PREFERENCE_NAME
import com.example.sheetcompute.entities.WEEKEND_DAYS_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.util.Calendar

object PreferencesGateway {
    val pref: SharedPreferences by lazy {
        DomainIntegration
            .getApplication()
            .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    private val _weekendDays = MutableStateFlow(emptySet<Int>())
    val weekendDays: StateFlow<Set<Int>> = _weekendDays
    //todo save the workingstart time
    fun saveWorkStartTime(time: LocalTime) {
        pref.edit().putString(KEY_WORK_START_TIME, time.toString()).apply()
    }

    fun getWorkStartTime(defaultTime: LocalTime = LocalTime.of(8, 30)): LocalTime {
        return try {
            LocalTime.parse(pref.getString(KEY_WORK_START_TIME, defaultTime.toString()))
        } catch (e: Exception) {
            defaultTime
        }
    }
    suspend fun init() = withContext(Dispatchers.IO) {
        _weekendDays.value = getWeekendDays() ?: setOf(Calendar.FRIDAY).also(::setWeekendDays)
    }
    // Save weekend days as comma-separated string (e.g., "FRIDAY,SATURDAY")
    fun setWeekendDays(days: Set<Int>) {
        val value = days.joinToString(",") { it.toString() }
        _weekendDays.value = days
        pref.edit { putString(WEEKEND_DAYS_KEY, value) }
    }

    // Get weekend days
    private fun getWeekendDays(): Set<Int>? {
        val saved = pref.getString(WEEKEND_DAYS_KEY, null) ?: return null
        return saved.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { runCatching { it.toInt()}.getOrNull() }
            .toSet()
    }

//todo save the month start day
    fun setMonthStartDay(day: Int) {
        pref.edit { putInt(MONTH_START_DAY_KEY, day) }
    }

    fun getMonthStartDay(): Int {
        return pref.getInt(MONTH_START_DAY_KEY, DEFAULT_MONTH_START_DAY)
    }

}