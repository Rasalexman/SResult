plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("kotlin-kapt")
}

android {
    compileSdk = config.Builds.COMPILE_VERSION
    defaultConfig {
        minSdk = config.Builds.MIN_VERSION
        targetSdk = config.Builds.TARGET_VERSION
        //versionCode = config.Builds.SResult.VERSION_CODE
        version = config.Builds.SResult.VERSION_NAME
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        this.resources.excludes.add("META-INF/notice.txt")
    }

    // Declare the task that will monitor all configurations.
    /*configurations.all {
        // 2 Define the resolution strategy in case of conflicts.
        resolutionStrategy {
            // Fail eagerly on version conflict (includes transitive dependencies),
            // e.g., multiple different versions of the same dependency (group and name are equal).
            failOnVersionConflict()

            // Prefer modules that are part of this build (multi-project or composite build) over external modules.
            preferProjectModules()
        }
    }*/

    sourceSets {
        getByName("main") {
            java.setSrcDirs(config.Builds.codeDirs)
        }
    }

    /*dexOptions {
        javaMaxHeapSize = "4g"
    }*/

    buildFeatures {
        dataBinding = true
        androidResources = true
    }

    kotlinOptions {
        jvmTarget = "11"
        languageVersion = "1.5"
        apiVersion = "1.5"
    }
}

kapt {
    useBuildCache = true
    generateStubs = false
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    //implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))
    api(config.Libs.Core.coroutines)

    //api(config.Libs.Core.coreKtx)
    api(config.Libs.Core.material)
    api(config.Libs.Core.constraintLayout)
    api(config.Libs.Core.fragment_ktx)
    api(config.Libs.Core.navigationFragmentKtx)
    api(config.Libs.Core.paging3)
    //api(config.Libs.Core.viewPager2)
    //api(config.Libs.Common.timber)

    api(config.Libs.Lifecycle.livedataKtx)
    api(config.Libs.Lifecycle.viewmodelKtx)


    //api(config.Libs.Lifecycle.savedStateViewModel)
    //api(config.Libs.Lifecycle.common)

    api(config.Libs.Common.easyRecyclerBinding)

    api(project(":sresult"))

    testImplementation(config.Libs.Tests.junit)
    androidTestImplementation(config.Libs.Tests.runner)
    androidTestImplementation(config.Libs.Tests.espresso)
}

group = "com.rasalexman.sresultpresentation"
version = config.Builds.SResult.VERSION_NAME

tasks.create(name = "sourceJar", type = Jar::class) {
    archiveClassifier.set("sources")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                //println("Component ${components.asMap}")
                from(components["release"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.sresultpresentation"
                artifactId = "sresultpresentation"
                version = config.Builds.SResult.VERSION_NAME

                artifact(tasks["sourceJar"])
            }
            create<MavenPublication>("debug") {
                //println("Component ${components.asMap}")
                from(components["debug"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.sresultpresentation"
                artifactId = "sresultpresentation-debug"
                version = config.Builds.SResult.VERSION_NAME

                artifact(tasks["sourceJar"])
            }
        }

        repositories {
            maven {
                name = "sresultpresentation"
                setUrl(layout.buildDirectory.dir("repo-presentation").toString())
            }
        }
    }
}