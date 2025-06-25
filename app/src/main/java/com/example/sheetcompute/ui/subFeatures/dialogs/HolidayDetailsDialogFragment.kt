package com.example.sheetcompute.ui.subFeatures.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.sheetcompute.R
import com.example.sheetcompute.data.entities.Holiday
import java.time.LocalDate

class HolidayDetailsDialogFragment private constructor(
    private val mode: DialogMode,
    private val startDate: LocalDate? = null,
    private val endDate: LocalDate? = null,
    private val existingHoliday: Holiday? = null,
    private val onHolidayAction: (Holiday) -> Unit
) : DialogFragment() {

    enum class DialogMode {
        ADD, EDIT
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = layoutInflater.inflate(R.layout.dialog_holiday_details, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.etHolidayName)
        val noteEditText = dialogView.findViewById<EditText>(R.id.etHolidayNote)

        // Pre-populate fields for edit mode
        if (mode == DialogMode.EDIT && existingHoliday != null) {
            nameEditText.setText(existingHoliday.name)
            noteEditText.setText(existingHoliday.note)
        }

        val title = when (mode) {
            DialogMode.ADD -> getString(R.string.add_holiday)
            DialogMode.EDIT -> getString(R.string.edit_holiday)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save), null)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                if (name.isEmpty()) {
                    nameEditText.error = getString(R.string.holiday_name_required)
                } else {
                    val holiday = when (mode) {
                        DialogMode.ADD -> Holiday(
                            startDate = startDate!!,
                            endDate = endDate!!,
                            name = name,
                            note = noteEditText.text.toString().trim()
                        )
                        DialogMode.EDIT -> existingHoliday!!.copy(
                            name = name,
                            note = noteEditText.text.toString().trim()
                        )
                    }
                    onHolidayAction(holiday)
                    dialog.dismiss()
                }
            }
        }

        return dialog
    }

    companion object {
        fun showAddDialog(
            fragmentManager: FragmentManager,
            startDate: LocalDate,
            endDate: LocalDate,
            onHolidayCreated: (Holiday) -> Unit
        ) {
            val dialogFragment = HolidayDetailsDialogFragment(
                mode = DialogMode.ADD,
                startDate = startDate,
                endDate = endDate,
                onHolidayAction = onHolidayCreated
            )
            dialogFragment.show(fragmentManager, "HolidayDetailsDialog")
        }

        fun showEditDialog(
            fragmentManager: FragmentManager,
            holiday: Holiday,
            onHolidayUpdated: (Holiday) -> Unit
        ) {
            val dialogFragment = HolidayDetailsDialogFragment(
                mode = DialogMode.EDIT,
                existingHoliday = holiday,
                onHolidayAction = onHolidayUpdated
            )
            dialogFragment.show(fragmentManager, "HolidayDetailsDialog")
        }
    }
}