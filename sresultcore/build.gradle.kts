plugins {
    id("java-library")
    kotlin("jvm")
}

val codePath: String by rootProject.extra
val appVersion: String by rootProject.extra

group = "com.rasalexman.sresultcore"
version = appVersion

val srcDirs = listOf(codePath)
sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        this.apiVersion = "1.6"
        this.languageVersion = "1.6"
        this.jvmTarget = "11"
    }
}

tasks.register<Jar>(name = "sourceJar") {
    from(sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}