package com.example.mazady

import android.app.Application
import com.example.mazady.di.dataModule
import com.example.mazady.di.viewModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(dataModule, viewModule)
            androidContext(this@App)
        }
    }
}