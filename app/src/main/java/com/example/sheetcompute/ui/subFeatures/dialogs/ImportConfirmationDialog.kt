package com.example.sheetcompute.ui.subFeatures.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.databinding.DialogImportConfirmationBinding
import com.example.sheetcompute.ui.subFeatures.utils.saveXlsTemplateToDownloads
import java.time.LocalTime

class ImportConfirmationDialog(
    context: Context,
    private val onConfirm: () -> Unit,
) : Dialog(context) {
    private lateinit var binding: DialogImportConfirmationBinding
    private var startTime: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogImportConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPickTime.setOnClickListener {
            val current = try { LocalTime.parse(startTime) } catch (e: Exception) { LocalTime.of(8,0) }
            showTimePickerDialog(context, current) { selectedTime ->
                startTime = selectedTime.toString()
                PreferencesGateway.saveWorkStartTime(selectedTime)
                binding.textStartTime.text = startTime
            }
        }
        binding.buttonDownloadTemplate.setOnClickListener {
            saveXlsTemplateToDownloads(context)
        }
        binding.buttonConfirm.setOnClickListener {
            onConfirm()
            dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun show() {
        super.show()
        // Always fetch the latest value from shared preferences when dialog is shown
        startTime = PreferencesGateway.getWorkStartTime().toString()
        binding.textStartTime.text = startTime
    }
}