package com.example.sheetcompute.ui.subFeatures.utils


import android.util.Log
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

object DateUtils {
    private val TIME_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
     fun formatMinutesToHoursMinutes(minutes: Long): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }
     fun formatTimeForStorage(time: LocalTime): String {
        return time.format(TIME_DISPLAY_FORMATTER)
            .replace("AM", " AM")  // Ensure space before AM
            .replace("PM", " PM")  // Ensure space before PM
    }

     fun parseTimeString(timeStr: String): LocalTime? {
        val cleanStr = timeStr
            .replace(" ", "")
            .replace(".", "")
            .uppercase()

        return try {
            when {
                cleanStr.contains("AM") || cleanStr.contains("PM") -> {
                    // Handle "6:29AM" or "6:29:59AM"
                    val format = if (cleanStr.count { it == ':' } == 2) "h:mm:ssa" else "h:mma"
                    LocalTime.parse(cleanStr, DateTimeFormatter.ofPattern(format, Locale.US))
                }

                cleanStr.count { it == ':' } == 2 -> {
                    // Handle "06:29:59"
                    LocalTime.parse(cleanStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
                }

                else -> {
                    // Handle "06:29"
                    LocalTime.parse(cleanStr, DateTimeFormatter.ofPattern("HH:mm"))
                }
            }
        } catch (e: Exception) {
            Log.w("ExcelImport", "Failed to parse time string: $timeStr' (cleaned: '$cleanStr')")
            null
        }
    }

fun parseDateSafely(dateStr: String): LocalDate? {
    return try {
        listOf(
            "dd-MMM-yyyy",   // 10-Jun-2025
            "dd-MM-yyyy",    // 10-06-2025
            "yyyy-MM-dd",    // 2025-06-10
            "MM/dd/yyyy",    // 06/10/2025
            "dd/MM/yyyy",    // 10/06/2025
            "M/d/yy",        // 6/4/25
            "MM/dd/yy",      // 06/04/25
            "M/d/yyyy"       // 6/4/2025
        ).firstNotNullOfOrNull { pattern ->
            try {
                LocalDate.parse(
                    dateStr,
                    DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                )
            } catch (e: Exception) {
                null
            }
        } ?: throw DateTimeParseException("No valid format found", dateStr, 0)
    } catch (e: Exception) {
        Log.w("DateParse", "Failed to parse '$dateStr': ${e.message}")
        null
    }
}
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
    fun formatDateRange(startDate: LocalDate, endDate: LocalDate, locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", locale)
        return "${startDate.format(formatter)} - ${endDate.format(formatter)}"
    }

    // Extension function for better date range formatting
    fun formatDateRange(startDate: java.time.LocalDate, endDate: java.time.LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

        return when {
            startDate == endDate -> startDate.format(formatter)
            startDate.year == endDate.year && startDate.month == endDate.month -> {
                // Same month: "Jan 15 - 20, 2024"
                "${startDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${endDate.format(formatter)}"
            }
            startDate.year == endDate.year -> {
                // Same year: "Jan 15 - Feb 20, 2024"
                "${startDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${endDate.format(formatter)}"
            }
            else -> {
                // Different years: "Dec 25, 2023 - Jan 5, 2024"
                "${startDate.format(formatter)} - ${endDate.format(formatter)}"
            }
        }
    }
}
