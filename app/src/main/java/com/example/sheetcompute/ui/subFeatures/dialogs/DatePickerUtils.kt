package com.example.sheetcompute.ui.subFeatures.dialogs

import android.util.Log
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object DatePickerUtils {

    fun showSingleDayPickerDialog(
        fragmentManager: FragmentManager,
        onDateSelected: (selectedDate: LocalDate) -> Unit
    ) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { millis ->
            val selectedDate = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            onDateSelected(selectedDate)
        }

        datePicker.addOnNegativeButtonClickListener {
            Log.d("DatePickerUtils", "Single Day Picker Cancelled")
        }

        datePicker.show(fragmentManager, "single_day_picker")
    }

    fun showRangePickerDialog(
        fragmentManager: FragmentManager,
        onDateRangeSelected: (startDate: LocalDate, endDate: LocalDate) -> Unit
    ) {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val pair = selection as Pair<Long, Long>
            val startDate = Instant.ofEpochMilli(pair.first)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val endDate = Instant.ofEpochMilli(pair.second)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            onDateRangeSelected(startDate, endDate)
        }

        datePicker.addOnNegativeButtonClickListener {
            Log.d("DatePickerUtils", "Date Range Picker Cancelled")
        }

        datePicker.show(fragmentManager, "range_picker")
    }
}