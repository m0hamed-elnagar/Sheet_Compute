package com.example.sheetcompute.domain.excel

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class FilePickerFragmentHelper(private val fragment: Fragment) {
    private var onFilePicked: ((InputStream) -> Unit)? = null
    private var onError: ((Exception) -> Unit)? = null

    private val filePickerLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        try {
            uri?.let {
                fragment.requireContext().contentResolver.openInputStream(uri)?.let { stream ->
                    onFilePicked?.invoke(stream)
                } ?: throw IOException("Couldn't open file stream")
            } ?: throw CancellationException("File selection cancelled")
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }

    fun pickExcelFile(
        onFilePicked: (InputStream) -> Unit,
        onError: (Exception) -> Unit
    ) {
        this.onFilePicked = onFilePicked
        this.onError = onError
        filePickerLauncher.launch("application/vnd.ms-excel")
    }
}