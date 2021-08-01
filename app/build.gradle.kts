import config.Builds
import config.Libs

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
}

android {
    compileSdk = Builds.COMPILE_VERSION
    buildToolsVersion = Builds.BUILD_TOOLS
    defaultConfig {
        applicationId = Builds.APP_ID
        minSdk = Builds.MIN_VERSION
        targetSdk = Builds.TARGET_VERSION
        versionCode = Builds.App.VERSION_CODE
        versionName = Builds.App.VERSION_NAME
        //testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = config.Versions.compose
        kotlinCompilerVersion = config.Versions.kotlin
    }

    packagingOptions {
        exclude("META-INF/notice.txt")
        exclude("/META-INF/{AL2.0,LGPL2.1}")
    }

    // Declare the task that will monitor all configurations.
    configurations.all {
        // 2 Define the resolution strategy in case of conflicts.
        resolutionStrategy {
            // Fail eagerly on version conflict (includes transitive dependencies),
            // e.g., multiple different versions of the same dependency (group and name are equal).
            failOnVersionConflict()

            // Prefer modules that are part of this build (multi-project or composite build) over external modules.
            preferProjectModules()
        }
    }

    buildFeatures {
        dataBinding = true
        compose = true
    }

    kotlinOptions {
        languageVersion = "1.5"
        apiVersion = "1.5"
    }

    kapt {
        useBuildCache = true
        generateStubs = false
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))

    //implementation("androidx.navigation:navigation-compose:2.4.0-alpha05")

    //implementation("androidx.activity:activity-compose:1.3.0")

    //------ Compose Core
    implementation("androidx.compose.ui:ui:${config.Versions.compose}")
    implementation("com.google.android.material:compose-theme-adapter:${config.Versions.compose}")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:${config.Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling-preview:${config.Versions.compose}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${config.Versions.compose}-alpha07")
    //implementation("androidx.compose.runtime:runtime-livedata:${config.Versions.compose}")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    //implementation("androidx.compose.foundation:foundation:1.0.0")
    // Material Design
    implementation("androidx.compose.material:material:${config.Versions.compose}")

    implementation(project(":sresultpresentation"))

    debugImplementation(Libs.Common.leakCanary)
    testImplementation(Libs.Tests.junit)
    androidTestImplementation(Libs.Tests.runner)
    androidTestImplementation(Libs.Tests.espresso)
}