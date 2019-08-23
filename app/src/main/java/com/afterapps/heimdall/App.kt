package com.afterapps.heimdall

import android.app.Application
import com.afterapps.heimdall.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused")
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(dataModule, collectionsModule, imagesModule, galleryModule, searchModule))
        }
    }
}