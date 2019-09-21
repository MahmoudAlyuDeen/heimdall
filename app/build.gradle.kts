plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)
    buildToolsVersion = "29.0.2"
    defaultConfig {
        applicationId = "com.afterapps.heimdall"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            isTestCoverageEnabled = true
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    androidExtensions {
        isExperimental = true
    }
    dataBinding {
        isEnabled = true
    }

    /** Token for Shutterstock API */
    buildTypes.forEach {
        it.buildConfigField(
            "String",
            "ShutterstockApiToken",
            properties["token"] as String
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        testInstrumentationRunner = "com.afterapps.heimdall.TestAppJUnitRunner"
    }

    /** To run Robolectric tests with android resources */
    testOptions.unitTests.isIncludeAndroidResources = true

    /** Allow IntelliJ coverage runner to collect coverage results from Robolectric tests */
    testOptions {
        unitTests.all(KotlinClosure1<Any, Test>({
            (this as Test).also { jvmArgs = listOf("-noverify") }
        }, this))
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(Libs.kotlin_stdlib_jdk7)

    // Koin
    implementation(Libs.koin_android_viewmodel)

    // AndroidX Fragment
    implementation(Libs.fragment)

    // Constraint Layout
    implementation(Libs.constraintlayout)

    // ViewModel and LiveData
    implementation(Libs.lifecycle_extensions)

    // Navigation
    implementation(Libs.navigation_fragment_ktx)
    implementation(Libs.navigation_ui_ktx)

    // Core with Ktx
    implementation(Libs.androidx_core_core_ktx)

    // Room
    implementation(Libs.room_runtime)
    kapt(Libs.room_compiler)

    // Moshi
    implementation(Libs.moshi)
    implementation(Libs.moshi_kotlin)

    // Retrofit with Moshi Converter
    implementation(Libs.retrofit)
    implementation(Libs.converter_moshi)

    // Coroutines
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_android)

    // Retrofit Coroutines Support
    implementation(Libs.retrofit2_kotlin_coroutines_adapter)

    // Glide
    implementation(Libs.glide)

    // RecyclerView
    implementation(Libs.recyclerview)

    // Paging
    implementation(Libs.paging_runtime_ktx)

    // Touch ImageView
    implementation(Libs.touchimageview)

    // Unit testing
    testImplementation(Libs.mockk)
    testImplementation(Libs.junit)
    testImplementation(Libs.androidx_arch_core_core_testing)
    testImplementation(Libs.kotlinx_coroutines_android)
    testImplementation(Libs.kotlinx_coroutines_test)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.espresso_core)
    testImplementation(Libs.espresso_contrib)
    testImplementation(Libs.espresso_intents)
    testImplementation(Libs.truth)
    testImplementation(Libs.koin_test)
    testImplementation(Libs.androidx_test_core_ktx)
    testImplementation(Libs.androidx_test_rules)
    testImplementation(Libs.junit_ktx)

    /**
     *  For fragment scenario testing.
     *  issue: https://issuetracker.google.com/127986458
     *  When fixed, replace [debugImplementation] with [testImplementation]
     *  */
    debugImplementation(Libs.fragment_testing)
    debugImplementation(Libs.androidx_test_core)

    // Instrumentation testing
    androidTestImplementation(Libs.koin_test)
    androidTestImplementation(Libs.junit)
    androidTestImplementation(Libs.kotlinx_coroutines_test)
    androidTestImplementation(Libs.truth)
    androidTestImplementation(Libs.androidx_test_core_ktx)
    androidTestImplementation(Libs.androidx_test_rules)
    androidTestImplementation(Libs.androidx_arch_core_core_testing)
    androidTestImplementation(Libs.org_robolectric_annotations)
    androidTestImplementation(Libs.espresso_core)
    androidTestImplementation(Libs.espresso_contrib)
    androidTestImplementation(Libs.mockk_android) {
        // kotlin-reflect is a transitive dependency of moshi-kotlin with a different version
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
}
