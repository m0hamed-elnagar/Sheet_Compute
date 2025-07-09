package com.example.sheetcompute.ui.subFeatures.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

 fun openExcelFile(context: Context, uri: Uri, mimeType: String) {
    val openIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        context.startActivity(openIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "No app found to open XLS file.", Toast.LENGTH_LONG).show()
    }
}
