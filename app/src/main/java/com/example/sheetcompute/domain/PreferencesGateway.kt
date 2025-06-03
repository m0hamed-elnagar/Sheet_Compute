package com.example.sheetcompute.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.sheetcompute.data.local.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.util.Calendar
import javax.inject.Inject
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

}