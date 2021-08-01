rootProject.buildFileName = "build.gradle.kts"
rootProject.name = "SResultExample"
include(":app", ":sresult", ":sresultpresentation")

buildCache {
    local {
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
    }
}
