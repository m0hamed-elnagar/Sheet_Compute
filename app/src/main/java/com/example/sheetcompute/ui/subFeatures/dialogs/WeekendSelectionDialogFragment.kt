package com.example.sheetcompute.ui.subFeatures.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.time.DayOfWeek

class WeekendSelectionDialogFragment(
    private val onDaysSelected: (Set<DayOfWeek>) -> Unit,
    private val initialSelectedDays: Set<DayOfWeek>? = null
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val daysOfWeek = DayOfWeek.entries
        val checkedItems = BooleanArray(daysOfWeek.size)
        val selectedDays = mutableSetOf<DayOfWeek>()

        // Initialize checked items based on initialSelectedDays
        initialSelectedDays?.forEach { day ->
            val index = daysOfWeek.indexOf(day)
            if (index != -1) {
                checkedItems[index] = true
                selectedDays.add(day)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Select Weekend Days")
            .setMultiChoiceItems(
                daysOfWeek.map { it.name }.toTypedArray(),
                checkedItems
            ) { _, which, isChecked ->
                val day = daysOfWeek[which]
                if (isChecked) {
                    selectedDays.add(day)
                } else {
                    selectedDays.remove(day)
                }
            }
            .setPositiveButton("OK") { _, _ ->
                onDaysSelected(selectedDays)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            initialSelectedDays: Set<DayOfWeek>? = null,
            onDaysSelected: (Set<DayOfWeek>) -> Unit
        ) {
            val dialogFragment = WeekendSelectionDialogFragment(onDaysSelected, initialSelectedDays)
            dialogFragment.show(fragmentManager, "WeekendSelectionDialog")
        }
    }
}