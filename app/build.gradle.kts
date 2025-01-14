plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    //id("com.android.application") // or "com.android.library"
    //id("org.jetbrains.kotlin.android") // Kotlin plugin
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    alias(libs.plugins.google.gms.google.services) // KSP plugin
}

android {
    namespace = "com.example.didit"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.didit"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // LeakCanary dependency
    // debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.9.1")

    // Room Runtime
    implementation("androidx.room:room-runtime:2.6.0")

    // Room Kotlin Extensions and Coroutines Support
    implementation("androidx.room:room-ktx:2.6.0")
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth)

    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")


    // Accompanist Glide integration for Jetpack Compose
    // implementation (libs.accompanist.glide)

    // Room Compiler for KSP
    ksp("androidx.room:room-compiler:2.6.0")

    //implementation("com.github.skydoves:landscapist-glide:1.5.2")


    // Optional - Testing Room
    testImplementation("androidx.room:room-testing:2.6.0")

    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.core:core-ktx:1.12.0") // For NotificationCompat

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}