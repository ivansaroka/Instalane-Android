package com.infinix.instalane

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.infinix.instalane.utils.createNotificationChannel

class InstalaneApplication : Application() {

    companion object {
        lateinit var instance: InstalaneApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        createNotificationChannel()
    }
}