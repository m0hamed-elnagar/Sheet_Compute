package com.example.sheetcompute.ui.utils


import java.text.DateFormatSymbols

object DateUtils {
    fun getMonthName(monthNumber: Int): String {
        return DateFormatSymbols().months.getOrNull(monthNumber - 1) ?: "Unknown"
    }
}
