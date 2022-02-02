package config

object Builds {
    const val MIN_VERSION = 19
    const val COMPILE_VERSION = 31
    const val TARGET_VERSION = 31
    const val APP_ID = "com.rasalexman.sresultexample"

    const val kotlinSrcDir = "src/main/kotlin"
    val codeDirs = listOf(kotlinSrcDir)

    object App {
        const val VERSION_CODE = 10002
        const val VERSION_NAME = "1.0.2"
    }

    object SResult {
        const val VERSION_CODE = 103039
        const val VERSION_NAME = "1.3.39"
    }
}