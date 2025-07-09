package com.example.sheetcompute.ui.subFeatures.utils

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val WRITE_STORAGE_REQUEST_CODE = 1001

fun saveXlsTemplateToDownloads(context: Context) {
    // For API 26–28, request permission
    if (Build.VERSION.SDK_INT in 26..28) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_STORAGE_REQUEST_CODE
            )
            return // wait for user to accept
        }
    }

    val fileName = "template-28-11.xls"
    val mimeType = "application/vnd.ms-excel"

    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
    }

    val collectionUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Files.getContentUri("external")
    }

    val uri = resolver.insert(collectionUri, contentValues)

    if (uri != null) {
        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                context.assets.open("template-28-11.xls").use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Mark as done (only needed on API 29+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            Toast.makeText(context, "Template saved to Downloads.", Toast.LENGTH_SHORT).show()

            // Try to open the file
            openExcelFile(context, uri, mimeType)

        } catch (e: Exception) {
            Toast.makeText(context, "Failed to save file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    } else {
        Toast.makeText(context, "Unable to access Downloads folder.", Toast.LENGTH_SHORT).show()
    }
}

