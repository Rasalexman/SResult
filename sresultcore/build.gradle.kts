import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("maven-publish")
}

val appVersion: String by rootProject.extra
val mainGroupName: String by rootProject.extra
val buildSdkVersion: Int by rootProject.extra
val minSdkVersion: Int by rootProject.extra
val kotlinApiVersion: String by rootProject.extra
val jvmVersion: String by rootProject.extra
val sresultCoreName: String by rootProject.extra

group = mainGroupName
version = appVersion

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        this.apiVersion = kotlinApiVersion
        this.languageVersion = kotlinApiVersion
        this.jvmTarget = jvmVersion
    }
}

kotlin {
    val opSystem = OperatingSystem.current()

    android {
        publishLibraryVariants("release", "debug")
    }
    jvm()
    if (opSystem.isMacOsX) {
//        ios()
//        iosX64()
//        macosX64()
//        iosSimulatorArm64()

        val xcf = XCFramework()
        ios {
            binaries {
                framework {
                    baseName = sresultCoreName
                    version = appVersion
                    xcf.add(this)
                }
            }
        }

        iosSimulatorArm64 {
            binaries {
                framework {
                    baseName = sresultCoreName
                    version = appVersion
                    xcf.add(this)
                }
            }
        }

//        val iosPlatforms = listOf(
//            ios(),
//            iosX64(),
//            iosArm64(),
//            iosSimulatorArm64()
//        )
//        iosPlatforms.onEach { target ->
//            target.binaries {
//                framework {
//                    baseName = "$sresultCoreName-${target.name}"
//                    version = appVersion
//                    isStatic = false
//                    xcf.add(this)
//                }
//            }
//        }

        cocoapods {
            // Required properties
            // Specify the required Pod version here. Otherwise, the Gradle project version is used.
            version = appVersion
            summary = "SResult Core Multiplatform library (android + ios)"
            homepage = "https://github.com/Rasalexman/SResult/$sresultCoreName"

            // Optional properties
            // Configure the Pod name here instead of changing the Gradle project name
            name = sresultCoreName

            framework {
                // Required properties
                // Framework name configuration. Use this property instead of deprecated 'frameworkName'
                baseName = sresultCoreName

                // Optional properties
                // Dynamic framework support
                isStatic = false
                transitiveExport = false // This is default.
                // Bitcode embedding
                embedBitcode(org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode.BITCODE)
            }

            // Maps custom Xcode configuration to NativeBuildType
            xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] =
                org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
            xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] =
                org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
        }

        if (opSystem.isMacOsX) {
            val iosSimulatorArm64Main by getting
            val iosMain by getting {
                dependsOn(commonMain)
                iosSimulatorArm64Main.dependsOn(this)
            }
//            val iosX64Main by getting
//            val iosArm64Main by getting
//            val iosSimulatorArm64Main by getting
//
//            val iosMain by creating {
//                dependsOn(commonMain)
//                iosX64Main.dependsOn(this)
//                iosArm64Main.dependsOn(this)
//                iosSimulatorArm64Main.dependsOn(this)
//            }
        }
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

android {
    compileSdk = buildSdkVersion
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = minSdkVersion
        targetSdk = buildSdkVersion
        version = appVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.register<Jar>(name = "sourceJar") {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    sourceSets {
        create("main") {
            java.setSrcDirs(listOf("src/commonMain/kotlin"))
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

//    withJavadocJar()
//    withSourcesJar()
}

kotlin.targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java) {
    binaries.all {
        binaryOptions["freezing"] = "disabled"
    }
}

afterEvaluate {
    publishing {

//        val publicationsFromMainHost =
//            listOf("android", "ios", "jvm").map { it } + "kotlinMultiplatform"

        publications {

//            matching { it.name in publicationsFromMainHost }.all {
//                val targetPublication = this@all
//                tasks.withType<AbstractPublishToMaven>()
//                    .matching { it.publication == targetPublication }
//                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
//            }

            getByName<MavenPublication>("kotlinMultiplatform") {
                // You can then customize attributes of the publication as shown below.
                groupId = mainGroupName
                artifactId = sresultCoreName
                version = appVersion

                //from(components["kotlin"])
                //artifact(tasks["sourceJar"])

                // Provide artifacts information requited by Maven Central
                pom {
                    name.set("SResult Kmp library")
                    description.set("SResult Core Multiplatform library (android + ios)")
                    url.set("https://github.com/Rasalexman/SResult")

                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("Rasalexman")
                            name.set("Alexandr Minkin")
                            email.set("sphc@yandex.ru")
                        }
                    }
                    scm {
                        url.set("https://github.com/Rasalexman/SResult")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "sresultcore"
                setUrl(layout.buildDirectory.dir("repo").toString())
            }
        }
    }
}
