import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
}

android {
    namespace = "com.awcindia.chatapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.awcindia.chatapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load local properties
        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").inputStream())

        // Add BuildConfig fields
        buildConfigField(
            "long",
            "ZEGOCLOUD_APP_ID",
            localProperties.getProperty("ZEGOCLOUD_APP_ID", "0").toLong().toString()
        )
        buildConfigField(
            "String",
            "ZEGOCLOUD_APP_SIGN",
            "\"${localProperties.getProperty("ZEGOCLOUD_APP_SIGN", "")}\""
        )
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true // Ensure BuildConfig is enabled
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
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("androidx.browser:browser:1.2.0")

    implementation("androidx.core:core:1.13.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.media3.common)
    implementation(libs.play.services.cast.framework)
    kapt("com.github.bumptech.glide:compiler:4.15.1")
    implementation("androidx.paging:paging-runtime:3.3.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.fragment:fragment-ktx:1.8.2")

    implementation("io.agora.rtc:full-sdk:4.1.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation(libs.imagepicker)

    implementation("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+")
    implementation("com.google.firebase:firebase-messaging:23.2.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.messaging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.material:material:1.12.0")
}
