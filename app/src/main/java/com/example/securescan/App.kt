package com.example.securescan

import android.app.Application
import com.example.securescan.utilities.SignalManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}