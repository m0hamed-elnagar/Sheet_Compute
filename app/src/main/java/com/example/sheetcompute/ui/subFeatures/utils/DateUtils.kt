package com.example.sheetcompute.ui.subFeatures.utils


import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun getMonthName(monthNumber: Int): String {
        return DateFormatSymbols().months.getOrNull(monthNumber - 1) ?: "Unknown"
    }
    fun Date.format(pattern: String): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(this)

    /**
     * Converts total minutes (e.g. 570) to "HH:mm" format (e.g. "09:30")
     */
    fun minutesToTimeString(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return String.format("%02d:%02d", hours, remainingMinutes)
    }

    /**
     * Converts "HH:mm" time string (e.g. "09:30") to total minutes (e.g. 570)
     */
    fun timeStringToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return hours * 60 + minutes
    }
    internal fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return "Not selected"
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    fun formatDateRange(startDate: Long, endDate: Long): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return "${dateFormat.format(Date(startDate))} - ${dateFormat.format(Date(endDate))}"
    }
}
