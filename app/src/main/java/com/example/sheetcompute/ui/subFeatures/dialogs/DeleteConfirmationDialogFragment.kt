package com.example.sheetcompute.ui.subFeatures.dialogs


import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.sheetcompute.R

class DeleteConfirmationDialogFragment(
    private val title: String,
    private val message: String,
    private val onConfirmed: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                onConfirmed()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            title: String,
            message: String,
            onConfirmed: () -> Unit
        ) {
            val dialogFragment = DeleteConfirmationDialogFragment(title, message, onConfirmed)
            dialogFragment.show(fragmentManager, "DeleteConfirmationDialog")
        }
    }
}