// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}

@Suppress("DSL_SCOPE_VIOLATION") plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.nav.safeArgs) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.firebase) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}


allprojects {
    apply(plugin= "io.gitlab.arturbosch.detekt")
    detekt {
        buildUponDefaultConfig = true
        config.setFrom( files("$rootDir/gradle/detekt.yml"))
        version="1.23.1"
    }
}



