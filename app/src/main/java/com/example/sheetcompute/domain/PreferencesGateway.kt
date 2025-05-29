package com.example.sheetcompute.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.sheetcompute.data.local.entities.*
import java.time.DayOfWeek
import javax.inject.Inject
object PreferencesGateway {
    val pref: SharedPreferences by lazy {
        DomainIntegration
            .getApplication()
            .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }


    init {
        // Set default value on first run
        if (pref.getBoolean(FIRST_RUN_KEY, true)) {
            setWeekendDays(setOf(DayOfWeek.FRIDAY))
            pref.edit { putBoolean(FIRST_RUN_KEY, false) }
        }
    }

    // Save weekend days as comma-separated string (e.g., "FRIDAY,SATURDAY")
    fun setWeekendDays(days: Set<DayOfWeek>) {
        val value = days.joinToString(",") { it.name }
        pref.edit { putString(WEEKEND_DAYS_KEY, value) }
    }

    // Get weekend days
    fun getWeekendDays(): Set<DayOfWeek>? {
        val saved = pref.getString(WEEKEND_DAYS_KEY, null) ?: return null
        return saved.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { runCatching { DayOfWeek.valueOf(it) }.getOrNull() }
            .toSet()
    }

}