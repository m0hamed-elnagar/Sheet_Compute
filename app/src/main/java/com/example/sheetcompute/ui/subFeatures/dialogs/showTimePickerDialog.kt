package com.example.sheetcompute.ui.subFeatures.dialogs

import android.content.Context
import android.widget.TimePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalTime


fun showTimePickerDialog(
    context: Context,
    currentStartTime: LocalTime? = null,
    workStartTime:(time: LocalTime)-> Unit,) {
    val timePicker = TimePicker(context).apply {
        hour = currentStartTime?.hour ?: LocalTime.now().hour
        minute = currentStartTime?.minute?: LocalTime.now().minute
        setIs24HourView(false)
    }

    MaterialAlertDialogBuilder(context)
        .setTitle("Select Work Start Time")
        .setView(timePicker)
        .setPositiveButton("Set Time") { _, _ ->
            val selectedTime = LocalTime.of(timePicker.hour, timePicker.minute)
            workStartTime(selectedTime)
        }
        .setNegativeButton("Cancel", null)
        .show()
}