package com.example.sheetcompute.ui.subFeatures.helpers

import android.content.Context
import com.example.sheetcompute.R
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.ui.subFeatures.dialogs.ImportConfirmationDialog
import com.example.sheetcompute.ui.subFeatures.dialogs.ImportResultDialog
import com.example.sheetcompute.ui.subFeatures.sheetPicker.FilePickerFragmentHelper
import com.example.sheetcompute.ui.subFeatures.utils.isInternetAvailable
import com.example.sheetcompute.ui.subFeatures.utils.showToast
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
 import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ExcelImportHelper(
    private val context: Context,
    private val preferencesGateway: PreferencesGateway,
    private val filePickerHelper: FilePickerFragmentHelper,
    private val coroutineScope: CoroutineScope,
    private val onImportExcel: suspend (
        inputStream: java.io.InputStream,
        onComplete: (String, ExcelImporter.ImportResult?) -> Unit,
        onError: (String) -> Unit
    ) -> Unit
) {
    fun showImportDialog() {
        ImportConfirmationDialog(context, preferencesGateway, onConfirm = {
            extractExcel()
        }).show()
    }

    private fun extractExcel() {
        if (isInternetAvailable(context)) {
            val isExcelEnabled = Firebase.remoteConfig.getBoolean("excel_enabled")
            if (isExcelEnabled) {
                showFilePicker()
            } else {
                showToast(context, context.getString(R.string.feature_not_available_for_now))
            }
        } else {
            showToast(context, context.getString(R.string.no_internet_connection))
        }
    }

    private fun showFilePicker() {
        filePickerHelper.pickExcelFile(
            onFilePicked = { inputStream ->
                coroutineScope.launch {
                    onImportExcel(
                        inputStream,
                        { message, importResult ->
                            if (importResult != null) {
                                ImportResultDialog(context, importResult).show()
                            } else {
                                showToast(context, message)
                            }
                        },
                         { errorMessage ->
                            showToast(context, errorMessage)
                        }
                    )
                }
            },
            onError = { exception ->
                showToast(context, exception?.message ?: "unknown_error")
            }
        )
    }
}
