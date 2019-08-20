package com.afterapps.heimdall

import android.app.Application
import com.afterapps.heimdall.di.collectionsModule
import com.afterapps.heimdall.di.dataModule
import com.afterapps.heimdall.di.galleryModule
import com.afterapps.heimdall.di.imagesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused")
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(dataModule, collectionsModule, imagesModule, galleryModule))
        }
    }
}