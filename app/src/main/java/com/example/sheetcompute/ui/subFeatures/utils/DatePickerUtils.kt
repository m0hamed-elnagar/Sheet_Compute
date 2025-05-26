package com.example.sheetcompute.ui.subFeatures.utils

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker

fun showSingleDayPickerDialog(
    fragmentManager: FragmentManager,
    onDateSelected: (selectedDate: Long) -> Unit
) {
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select Date")
        .build()

    datePicker.addOnPositiveButtonClickListener {
        onDateSelected(it)
    }

    datePicker.addOnNegativeButtonClickListener {
        Log.d("DatePickerUtils", "Single Day Picker Cancelled")
    }

    datePicker.show(fragmentManager, "single_day_picker")
}
