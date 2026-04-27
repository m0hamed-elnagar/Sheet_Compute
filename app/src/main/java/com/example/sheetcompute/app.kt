package com.example.sheetcompute

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initRemoteConfig()
    }
}
fun initRemoteConfig() {
    val config = Firebase.remoteConfig

    val settings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
    }
    config.setConfigSettingsAsync(settings)

    config.setDefaultsAsync(mapOf("excel_enabled" to true))

    config.fetchAndActivate()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("RemoteConfig", "Fetch succeeded")
            } else {
                Log.w("RemoteConfig", "Fetch failed, using defaults")
            }
        }
}