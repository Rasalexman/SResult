plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

val appVersion: String by rootProject.extra
val codePath: String by rootProject.extra
val mainGroupName: String by rootProject.extra
val kotlinApiVersion: String by rootProject.extra
val jvmVersion: String by rootProject.extra
group = mainGroupName
version = appVersion

android {
    val buildSdkVersion: Int by extra
    val minSdkVersion: Int by extra

    compileSdk = buildSdkVersion
    defaultConfig {
        minSdk = minSdkVersion
        targetSdk = buildSdkVersion
        version = appVersion

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
            java.setSrcDirs(listOf(codePath))
        }
    }

    kotlinOptions {
        jvmTarget = jvmVersion
        languageVersion = kotlinApiVersion
        apiVersion = kotlinApiVersion
    }
}

tasks.register<Jar>(name = "sourceJar") {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    sourceSets {
        create("main") {
            java.setSrcDirs(listOf(codePath))
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    //withJavadocJar()
    withSourcesJar()
}

dependencies {
    //implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    val timber: String by rootProject.extra
    val coroutinesmanager: String by rootProject.extra

    api(timber)
    api(coroutinesmanager)
    api(project(":sresultcore"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                // You can then customize attributes of the publication as shown below.
                groupId = mainGroupName
                artifactId = "sresult"
                version = appVersion

                //artifact("$buildDir/outputs/aar/sresult-release.aar")
                artifact(tasks["sourceJar"])
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
