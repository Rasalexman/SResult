plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

val appVersion: String by rootProject.extra
val mainGroupName: String by rootProject.extra
val codePath: String by rootProject.extra

tasks.register<Jar>(name = "sourceJar") {
    from(java.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    sourceSets {
        getByName("main") {
            java.setSrcDirs(listOf(codePath))
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("release") {
            //from(components["core"])

            // You can then customize attributes of the publication as shown below.
            groupId = mainGroupName
            artifactId = "sresultcore"
            version = appVersion

            //artifact("$buildDir/outputs/aar/sresult-release.aar")
            artifact(tasks["sourceJar"])
        }
    }

    repositories {
        maven {
            name = "sresultcore"
            setUrl(layout.buildDirectory.dir("repo").toString())
        }
    }
}