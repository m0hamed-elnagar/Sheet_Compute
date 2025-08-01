package com.example.sheetcompute.ui.subFeatures.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.sheetcompute.databinding.DialogImportResultBinding
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.domain.excel.export.RejectionWorkbookBuilder
import com.example.sheetcompute.ui.subFeatures.utils.ExcelFileSaver
import com.example.sheetcompute.ui.subFeatures.utils.shareXlsViaWhatsApp
import java.io.File

class ImportResultDialog(
    context: Context,
    private val importResult : ExcelImporter.ImportResult,
    private val onSaveReport: (() -> Unit)? = null,
    private val onShareWhatsapp: (() -> Unit)? = null
) : Dialog(context) {
    private lateinit var binding: DialogImportResultBinding
    private var errorFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogImportResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textAddedCount.text = "Added to database: ${importResult.recordsAdded}"
        binding.textDuplicateCount.text = "Duplicates not added: ${importResult.duplicates.size}"
        if (importResult.errors.isNotEmpty()) {
            val workbook = RejectionWorkbookBuilder.buildWorkbook(importResult.errors)
            errorFile = ExcelFileSaver.saveToCache(context, workbook)
            if (errorFile != null) {
                binding.buttonSaveReport.isEnabled = true
                binding.buttonShareWhatsapp.isEnabled = true
                binding.textErrorFile.text = "(${importResult.errors.size}) Errors exported: \n ${errorFile?.name}"
                binding.buttonSaveReport.setOnClickListener {
                    ExcelFileSaver.saveToDownloads(context, workbook)
                }
                binding.buttonShareWhatsapp.setOnClickListener {
                    errorFile?.let { file ->
                        shareXlsViaWhatsApp(context, file)
                    }
                }

            } else {
                binding.textErrorFile.text = "Failed to export errors."
                binding.buttonSaveReport.isEnabled = false
                binding.buttonShareWhatsapp.isEnabled = false
            }
        } else {
            binding.textErrorFile.text = "No error file generated."
            binding.buttonSaveReport.isEnabled = false
            binding.buttonShareWhatsapp.isEnabled = false
        }
        binding.buttonClose.setOnClickListener { dismiss() }
    }
}