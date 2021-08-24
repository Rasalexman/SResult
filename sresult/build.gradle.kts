import config.Builds
import config.Libs

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}

android {
    compileSdk = Builds.COMPILE_VERSION
    buildToolsVersion = Builds.BUILD_TOOLS
    defaultConfig {
        minSdk = Builds.MIN_VERSION
        targetSdk = Builds.TARGET_VERSION
        versionCode = Builds.SResult.VERSION_CODE
        versionName = Builds.SResult.VERSION_NAME
        multiDexEnabled = true
        //testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            //isDebuggable = true
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
        this.resources.excludes.add("META-INF/notice.txt")
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

    sourceSets {
        getByName("main") {
            java.setSrcDirs(Builds.codeDirs)
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.5"
        apiVersion = "1.5"
    }
}

/*java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(Builds.codeDirs)
    }
}*/

dependencies {
    //implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))

    api(Libs.Common.timber)
    api(Libs.Common.kodi)
    api(Libs.Common.coroutinesmanager)

    //testImplementation(Libs.Tests.junit)
    //androidTestImplementation(Libs.Tests.runner)
    //androidTestImplementation(Libs.Tests.espresso)
}

group = "com.rasalexman.sresult"
version = Builds.SResult.VERSION_NAME

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.sresult"
                artifactId = "sresult"
                version = Builds.SResult.VERSION_NAME
            }
            /*create<MavenPublication>("debug") {
                from(components["debug"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.sresult"
                artifactId = "sresult-debug"
                version = Builds.SResult.VERSION_NAME
            }*/
        }

        repositories {
            maven {
                name = "sresult"
                setUrl(layout.buildDirectory.dir("repo").toString())
            }
        }
    }
}
