package com.example.sheetcompute.domain

import android.app.Application
import com.example.sheetcompute.App

object DomainIntegration {

    private lateinit var applicationReference: Application

    fun with(application: Application) {
        applicationReference = (application)
    }

    fun getApplication() =
        applicationReference as App
}