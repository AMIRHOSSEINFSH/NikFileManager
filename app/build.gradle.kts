plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.detekt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.nav.safeArgs)
    alias(libs.plugins.kotlin.parcelize)
//    alias(libs.plugins.firebase)
//    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.android.filemanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.filemanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

//    api(project(mapOf("path" to ":common")))
    api(project(":common"))
    implementation(libs.hilt)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    kapt(libs.hilt.compiler)

    implementation(libs.assentPermission.handler)
    implementation(libs.assentPermission.handler.rationales)

    //navigation component
    implementation(libs.navFragment)
    implementation(libs.navUi)

    //preference DataStore
    implementation(libs.dataStore)
    implementation(libs.dataStore.preferences)

    //coroutine
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)

    //coroutine lifecycle scopes
    implementation(libs.lifecycle.viewmodel)

    //coil
    implementation(libs.coil)
    implementation(libs.coil.svg)
    /*
        implementation("io.coil-kt:coil-video:2.1.0")
    */

    implementation(libs.previewer)
    // Kotlin


    implementation(libs.segmented.control)

    implementation(libs.viewpager2)

    implementation(libs.power.spinner)

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

}