package com.afterapps.heimdall

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/** Bypassing our custom [App] to prevent dependency injection while running instrumental tests */
@Suppress("unused")
class TestAppJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, Application::class.java.name, context)
    }
}
