plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

val appVersion: String by rootProject.extra
val mainGroupName: String by rootProject.extra
val buildSdkVersion: Int by rootProject.extra
val minSdkVersion: Int by rootProject.extra
val kotlinApiVersion: String by rootProject.extra
val jvmVersion: String by rootProject.extra

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
    android {
        publishLibraryVariantsGroupedByFlavor = true
        publishLibraryVariants("release", "debug")
    }
    jvm()
    ios()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "sresultcore"
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

        val iosX64Main by getting
        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting

        val iosMain by getting {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            //iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        //val iosSimulatorArm64Test by getting

        val iosTest by getting {
            //dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            //iosSimulatorArm64Test.dependsOn(this)
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
    withSourcesJar()
}

kotlin.targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java) {
    binaries.all {
        binaryOptions["freezing"] = "disabled"
    }
}

afterEvaluate {
    publishing {

        val publicationsFromMainHost =
            listOf("android", "ios", "jvm").map { it } + "kotlinMultiplatform"

        publications {

            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
            }

            getByName<MavenPublication>("kotlinMultiplatform") {
                // You can then customize attributes of the publication as shown below.
                groupId = mainGroupName
                artifactId = "sresultcore"
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
