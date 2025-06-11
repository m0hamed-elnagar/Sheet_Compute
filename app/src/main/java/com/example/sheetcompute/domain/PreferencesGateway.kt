package com.example.sheetcompute.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.sheetcompute.data.entities.SHARED_PREFERENCE_NAME
import com.example.sheetcompute.data.entities.WEEKEND_DAYS_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.util.Calendar

object PreferencesGateway {
    val pref: SharedPreferences by lazy {
        DomainIntegration
            .getApplication()
            .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    private val _weekendDays = MutableStateFlow(emptySet<Int>())
    val weekendDays: StateFlow<Set<Int>> = _weekendDays
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

    private const val MONTH_START_DAY_KEY = "month_start_day"
    private const val DEFAULT_MONTH_START_DAY = 26

    fun setMonthStartDay(day: Int) {
        pref.edit { putInt(MONTH_START_DAY_KEY, day) }
    }

    fun getMonthStartDay(): Int {
        return pref.getInt(MONTH_START_DAY_KEY, DEFAULT_MONTH_START_DAY)
    }

}