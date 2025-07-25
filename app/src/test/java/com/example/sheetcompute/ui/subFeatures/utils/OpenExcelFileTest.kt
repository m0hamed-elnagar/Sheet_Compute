package com.example.sheetcompute.ui.subFeatures.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class OpenExcelFileTest {
    @Test
    fun `openExcelFile should start activity with correct intent`() {
        val context = mockk<Context>(relaxed = true)
        val uri = mockk<Uri>()
        val mimeType = "application/vnd.ms-excel"

        every { context.startActivity(any()) } returns Unit

        openExcelFile(context, uri, mimeType)

        verify { context.startActivity(any()) }
    }

    @Test
    fun `openExcelFile should show toast if no app found`() {
        val context = mockk<Context>(relaxed = true)
        val uri = mockk<Uri>()
        val mimeType = "application/vnd.ms-excel"

        every { context.startActivity(any()) } throws Exception()
        every { Toast.makeText(context, any<String>(), any()) } returns mockk(relaxed = true)

        openExcelFile(context, uri, mimeType)

        verify { Toast.makeText(context, "No app found to open XLS file.", Toast.LENGTH_LONG) }
    }
}

