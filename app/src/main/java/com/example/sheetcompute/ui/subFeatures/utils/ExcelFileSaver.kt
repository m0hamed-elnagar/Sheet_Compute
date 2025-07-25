package com.example.sheetcompute.ui.subFeatures.utils


import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExcelFileSaver {

    // Save to Downloads folder
    fun saveToDownloads(context: Context, workbook: HSSFWorkbook, baseName: String = "rejected_rows"): File? {
        val fileName = generateFileName(baseName)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        return writeWorkbook(context, file, workbook)
    }

    // Save to cache folder (for sharing)
    fun saveToCache(context: Context, workbook: HSSFWorkbook, baseName: String = "rejected_rows"): File? {
        val fileName = generateFileName(baseName)
        val file = File(context.cacheDir, fileName)
        return writeWorkbook(context, file, workbook)
    }@VisibleForTesting
    internal var nowProvider: () -> Date = { Date() }

    @VisibleForTesting
    internal fun generateFileName(base: String): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
            .format(nowProvider())
        return "$base-$timestamp.xls"
    }


    private fun writeWorkbook(context: Context, file: File, workbook: HSSFWorkbook): File? {
        return try {
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()
            Toast.makeText(context, "Excel file saved: ${file.name}", Toast.LENGTH_LONG).show()
            file
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }
}
