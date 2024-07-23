// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.navigation.safeargs) apply false
}

buildscript {
    dependencies {
        // https://mvnrepository.com/artifact/androidx.navigation.safeargs/androidx.navigation.safeargs.gradle.plugin
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
    }
}