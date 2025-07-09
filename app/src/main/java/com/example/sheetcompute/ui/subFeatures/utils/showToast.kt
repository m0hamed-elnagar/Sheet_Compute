package com.example.sheetcompute.ui.subFeatures.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

 fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
     Log.d("Toast", "Excel import completed: $message")

 }