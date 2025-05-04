package com.example.sheetcompute

import android.app.Application
import com.example.sheetcompute.domain.DomainIntegration

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DomainIntegration.with(this)
    }
}
