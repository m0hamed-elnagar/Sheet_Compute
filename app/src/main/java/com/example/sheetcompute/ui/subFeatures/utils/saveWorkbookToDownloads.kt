package com.example.sheetcompute.ui.subFeatures.utils


import android.content.Context
import android.os.Environment
import android.widget.Toast
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExcelFileSaver2 {

    fun saveWorkbookToDownloads(
        context: Context,
        workbook: HSSFWorkbook,
        baseFileName: String = "rejected_rows"
    ): File? {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        val fileName = "$baseFileName-$timestamp.xls"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        return try {
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()
            Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
            file
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }
}
