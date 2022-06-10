plugins {
    kotlin("multiplatform")
    id("com.android.library")
    //id("maven-publish")
}

val appVersion: String by rootProject.extra
val buildSdkVersion: Int by rootProject.extra
val minSdkVersion: Int by rootProject.extra

group = "com.rasalexman.sresultcore"
version = appVersion

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        this.apiVersion = "1.6"
        this.languageVersion = "1.6"
        this.jvmTarget = "11"
    }
}

//tasks.register<Jar>(name = "sourceJar") {
//    from(sourceSets["main"].java.srcDirs)
//    archiveClassifier.set("sources")
//}

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }
//    jvm {
//        compilations.all {
//            kotlinOptions {
//                this.apiVersion = "1.6"
//                this.languageVersion = "1.6"
//                this.jvmTarget = "11"
//            }
//        }
//    }
//    ios()

    sourceSets {
        val commonMain by getting
//        val commonTest by getting {
//            dependencies {
//                implementation(kotlin("test-common"))
//                implementation(kotlin("test-annotations-common"))
//            }
//        }
        val androidMain by getting {
            dependsOn(commonMain)
        }
//        val jvmTest by getting {
//            dependencies {
//                //implementation(kotlin("test-junit"))
//            }
//        }
//        val iosMain by getting {
//            dependsOn(commonMain)
//        }
        //val iosTest by getting
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

//    withJavadocJar()
//    withSourcesJar()
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

    sourceSets {
        getByName("main") {
            java.setSrcDirs(listOf("src/commonMain/kotlin"))
        }
    }
}

kotlin.targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java) {
    binaries.all {
        binaryOptions["freezing"] = "disabled"
    }
}

//publishing {
//
////    val publicationsFromMainHost =
////        listOf(android(), ios()).map { it.name } + "kotlinMultiplatform"
//
//    publications {
////        matching { it.name in publicationsFromMainHost }.all {
////            val targetPublication = this@all
////            tasks.withType<AbstractPublishToMaven>()
////                .matching { it.publication == targetPublication }
////                .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
////        }
//
//        // Configure all publications
//        publications.withType<MavenPublication> {
//            // You can then customize attributes of the publication as shown below.
//            groupId = "com.rasalexman.sresultcore"
//            artifactId = "kmp"
//            version = appVersion
//            // Stub javadoc.jar artifact
//            //artifact(javadocJar.get())
//
//            // Provide artifacts information requited by Maven Central
//            pom {
//                name.set("SResult Kmp library")
//                description.set("SResult Core Multiplatform library (android + ios)")
//                url.set("https://github.com/Rasalexman/SResult")
//
//                licenses {
//                    license {
//                        name.set("MIT")
//                        url.set("https://opensource.org/licenses/MIT")
//                    }
//                }
//                developers {
//                    developer {
//                        id.set("Rasalexman")
//                        name.set("Alexandr Minkin")
//                        email.set("sphc@yandex.ru")
//                    }
//                }
//                scm {
//                    url.set("https://github.com/Rasalexman/SResult")
//                }
//            }
//        }
//    }
//
//    repositories {
//        maven {
//            name = "sresultcore"
//            url = uri("${buildDir}/publishing-repository")
//        }
//    }
//}
