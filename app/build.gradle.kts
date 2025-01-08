plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.zen.accounts"
    compileSdk = 34
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.zen.accounts"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    implementation("androidx.test:core-ktx:1.6.1")
    implementation("androidx.test.ext:junit-ktx:1.2.1")
    testImplementation("junit:junit:4.13.2")
    val navVersion = "2.8.3"
    val workVersion = "2.9.1"
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material:1.7.4")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("com.google.android.material:material:1.12.0")

    // CameraX
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Data Store
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Gson and Moshi
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.moshi:moshi:1.15.1")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")

    // Lottie Animation
    implementation("com.airbnb.android:lottie-compose:4.0.0")

    // Work Manager
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("com.google.guava:guava:33.0.0-jre")

    // Image Cropper
    implementation("com.vanniktech:android-image-cropper:4.5.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.3.0")
    
    // Glide
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")

    // Testing Dependencies
    // Work Manager Test
    androidTestImplementation("androidx.work:work-testing:$workVersion")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")
    
    // Paging 3
    implementation("androidx.paging:paging-compose:3.3.2")
    
    // lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    
    // --------------------------- Testing Implementations --------------------------------------
    // Junit
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Truth
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("androidx.test.ext:truth:1.6.0")
    androidTestImplementation("com.google.truth:truth:1.4.4")
    androidTestImplementation("androidx.test.ext:truth:1.6.0")
    
    // Compose
    androidTestImplementation(composeBom)
    
    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Turbine
    runtimeOnly("app.cash.turbine:turbine:1.2.0")
    androidTestImplementation("app.cash.turbine:turbine:1.2.0")
    
    // Mockito-Kotlin
    testImplementation("org.mockito:mockito-core:5.14.2")
    androidTestImplementation("org.mockito:mockito-android:5.14.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    
    // Kotlin Coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    implementation(kotlin("reflect"))
}

kapt {
    correctErrorTypes = true
}
