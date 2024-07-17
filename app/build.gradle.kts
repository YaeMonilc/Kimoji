plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "moe.wisteria.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "moe.wisteria.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation(libs.retrofit)
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
    implementation(libs.converter.gson)
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation(libs.okhttp)
    // https://mvnrepository.com/artifact/io.coil-kt/coil
    implementation(libs.coil)
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation(libs.gson)

    // https://mvnrepository.com/artifact/androidx.datastore/datastore-preferences
    implementation(libs.androidx.datastore.preferences)
    // https://mvnrepository.com/artifact/androidx.navigation/navigation-fragment
    implementation(libs.androidx.navigation.fragment)
    // https://mvnrepository.com/artifact/androidx.navigation/navigation-ui-ktx
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}