package com.example.sheetcompute.ui.subFeatures.utils

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.core.util.Pair as AndroidPair
import com.google.android.material.datepicker.MaterialDatePicker

object DatePickerUtils {

    fun showRangePickerDialog(
        fragmentManager: FragmentManager,
        onDateRangeSelected: (startDate: Long, endDate: Long) -> Unit
    ) {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .build()

        datePicker.addOnPositiveButtonClickListener {
            try {
                val pair = it as AndroidPair<Long, Long>
                onDateRangeSelected(pair.first, pair.second)
            } catch (e: ClassCastException) {
                Log.e("DatePickerUtils", "Failed to cast selected date range to Pair<Long, Long>", e)
            }
        }

        datePicker.addOnNegativeButtonClickListener {
            Log.d("DatePickerUtils", "Date Range Picker Cancelled")
        }

        datePicker.show(fragmentManager, "range_picker")
    }
}