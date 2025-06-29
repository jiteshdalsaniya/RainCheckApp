plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")// Hilt plugin
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" // KSP for Moshi or others if used
    id("kotlin-kapt")// Kotlin annotation processing
}

android {
    namespace = "com.jd.raincheckapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jd.raincheckapp"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Core Android
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx.v1131)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose) // For ViewModel in Compose
    implementation(libs.androidx.material.icons.extended)

    // Location Services
    implementation(libs.play.services.location)

    // Networking (Retrofit, OkHttp, Gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    kapt(libs.moshi.kotlin.codegen)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Accompanist Permissions (for easier permission handling)
    implementation(libs.accompanist.permissions)

    // Date Picker (Material 3)
    implementation("androidx.compose.material3:material3-window-size-class:1.3.2")
    implementation(libs.material) // For Material DatePicker

    // Coil for image loading (if you plan to load weather icons)
    implementation(libs.coil.compose)
    // Dagger Hilt
    implementation(libs.hilt.android) // Latest Hilt version
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler) // Latest Hilt-Compose compiler
    implementation(libs.androidx.hilt.navigation.compose)


    // Testing (optional)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(libs.androidx.compose.bom.v20230800)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}