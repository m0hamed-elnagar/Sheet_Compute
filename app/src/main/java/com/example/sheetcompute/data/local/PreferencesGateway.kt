package com.example.sheetcompute.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.sheetcompute.data.entities.DEFAULT_MONTH_START_DAY
import com.example.sheetcompute.data.entities.KEY_WORK_START_TIME
import com.example.sheetcompute.data.entities.MONTH_START_DAY_KEY
import com.example.sheetcompute.data.entities.WEEKEND_DAYS_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesGateway @Inject constructor(
    val pref: SharedPreferences
) {
    private val _weekendDays = MutableStateFlow(emptySet<Int>())
    val weekendDays: StateFlow<Set<Int>> = _weekendDays

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

    private fun getWeekendDays(): Set<Int>? {
        val saved = pref.getString(WEEKEND_DAYS_KEY, null) ?: return null
        return saved.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { runCatching { it.toInt() }.getOrNull() }
            .toSet()
    }

    fun setMonthStartDay(day: Int) {
        pref.edit { putInt(MONTH_START_DAY_KEY, day) }
    }

    fun getMonthStartDay(): Int {
        return pref.getInt(MONTH_START_DAY_KEY, DEFAULT_MONTH_START_DAY)
    }
}
