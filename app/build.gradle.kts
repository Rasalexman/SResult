import config.Builds
import config.Libs

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
}

android {
    compileSdkVersion(Builds.COMPILE_VERSION)
    buildToolsVersion = Builds.BUILD_TOOLS
    defaultConfig {
        applicationId = Builds.APP_ID
        minSdkVersion(Builds.MIN_VERSION)
        targetSdkVersion(Builds.TARGET_VERSION)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/notice.txt")
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
        viewBinding = true
    }

    kotlinOptions {
        languageVersion = "1.5"
        apiVersion = "1.5"
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))

    implementation(Libs.Core.coreKtx)
    implementation(Libs.Core.material)
    implementation(Libs.Core.constraintlayout)
    implementation(project(":sresultpresentation"))

    implementation(Libs.Core.navigationFragmentKtx)

    testImplementation(Libs.Tests.junit)
    androidTestImplementation(Libs.Tests.runner)
    androidTestImplementation(Libs.Tests.espresso)
}