package com.example.sheetcompute.ui.subFeatures.utils


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
            .replace(Regex("\\s+"), " ")  // Normalize any extra spacing
            .trim()
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
                    LocalTime.parse(cleanStr, DateTimeFormatter.ofPattern("HH:mm"))
                }
            }
        } catch (e: Exception) {
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
        logWarning("Failed to parse date: '$dateStr' - ${e.message}")
        null
    }
}
    fun getMonthName(monthNumber: Int): String {
        val name = DateFormatSymbols().months.getOrNull(monthNumber - 1)
        return if (name.isNullOrBlank()) "Unknown" else name
    }

    fun Date.format(pattern: String): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun formatDateRange(startDate: LocalDate, endDate: LocalDate): String {
    val fullFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val shortFormatter = DateTimeFormatter.ofPattern("MMM d")

    return when {
        startDate == endDate -> startDate.format(fullFormatter)

        startDate.year == endDate.year && startDate.month == endDate.month -> {
            // Same month: "Jan 15 - 20, 2024"
            "${startDate.format(shortFormatter)} - ${endDate.dayOfMonth}, ${endDate.year}"
        }

        startDate.year == endDate.year -> {
            // Same year: "Jan 15 - Feb 20, 2024"
            "${startDate.format(shortFormatter)} - ${endDate.format(fullFormatter)}"
        }

        else -> {
            // Different years: "Dec 25, 2023 - Jan 5, 2024"
            "${startDate.format(fullFormatter)} - ${endDate.format(fullFormatter)}"
        }
    }
}

    // Safe logger (won’t crash in unit tests)
    private fun logWarning(message: String) {
        println("WARNING: $message") // Replace or mock in tests if needed
    }
}
