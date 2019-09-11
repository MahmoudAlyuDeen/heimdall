package com.afterapps.heimdall

/** Bypassing our custom [App] to prevent dependency injection while running uni tests */
@Suppress("unused")
class TestApp : App() {

    override fun onCreate() = Unit

}