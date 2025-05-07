package com.example.sheetcompute.ui.utils


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
}
