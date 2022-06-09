plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    kotlin("kapt")
}

val appVersion: String by rootProject.extra
val codePath: String by rootProject.extra
val supportGroupName: String by rootProject.extra
val apiVersion: String by rootProject.extra
val jvmVersion: String by rootProject.extra
group = supportGroupName
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
            java.srcDir(codePath)
        }
        getByName("release") {
            java.srcDir(codePath)
        }
        getByName("debug") {
            java.srcDir(codePath)
        }
    }

    buildFeatures {
        dataBinding = true
        //androidResources = true
    }

    kotlinOptions {
        jvmTarget = jvmVersion
        languageVersion = apiVersion
        apiVersion = apiVersion
    }
}

tasks.create(name = "sourceJar", type = Jar::class) {
    val dirs = android.sourceSets.getByName("main").java.srcDirs
    from(dirs)
    archiveClassifier.set("sources")
}

java {

    this.sourceSets.create("main").java {
        setSrcDirs(android.sourceSets.getByName("main").java.srcDirs.toList())
    }
    //println("-----> sources: ${this.sourceSets.getByName("main").allSource}")
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    //withJavadocJar()
    withSourcesJar()
}

dependencies {

    val viewpager2: String by rootProject.extra
    val recyclerview: String by rootProject.extra
    val coroutines: String by rootProject.extra
    val fragmentKtx: String by rootProject.extra
    val paging: String by rootProject.extra
    val constraintlayout: String by rootProject.extra
    val navigationFragment: String by rootProject.extra
    val material: String by rootProject.extra
    val livedataKtx: String by rootProject.extra
    val viewmodelKtx: String by rootProject.extra
    val easyrecyclerbinding: String by rootProject.extra
    val kodi: String by rootProject.extra

    api(coroutines)
    api(material)
    api(constraintlayout)
    api(fragmentKtx)
    api(navigationFragment)
    api(paging)
    api(viewpager2)
    api(recyclerview)
    api(livedataKtx)
    api(viewmodelKtx)

    api(easyrecyclerbinding)
    api(kodi)

    api(project(":sresult"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("main") {
                //println("-----> Components ${components["release"]}")
                from(components["release"])

                // You can then customize attributes of the publication as shown below.
                groupId = supportGroupName
                artifactId = "sresultpresentation"
                version = appVersion

                //artifact("$buildDir/outputs/aar/sresult-release.aar")
                artifact(tasks["sourceJar"])
            }
            /*create<MavenPublication>("debug") {
                //println("Component ${components.asMap}")
                from(components["debug"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.sresultpresentation"
                artifactId = "sresultpresentation-debug"
                version = appVersion

                //artifact("$buildDir/outputs/aar/sresult-debug.aar")
                artifact(tasks["sourceJar"])
            }*/
        }

        repositories {
            maven {
                name = "sresultpresentation"
                setUrl(layout.buildDirectory.dir("repo-presentation").toString())
            }
        }
    }
}