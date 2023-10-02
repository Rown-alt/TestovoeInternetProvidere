package com.example.testovoeinternetprovidere.di

import android.app.Application
import com.example.domain.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AirNetApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@AirNetApplication)
            modules(listOf(appModule, domainModule))
        }
    }
}