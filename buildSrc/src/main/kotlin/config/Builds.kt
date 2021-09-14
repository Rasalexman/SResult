package config

object Builds {
    const val MIN_VERSION = 21
    const val COMPILE_VERSION = 30
    const val TARGET_VERSION = 30
    const val BUILD_TOOLS = "30.0.2"
    const val APP_ID = "com.rasalexman.sresultexample"

    val codeDirs = mutableListOf(
        "src/main/kotlin"
    )

    object App {
        const val VERSION_CODE = 10001
        const val VERSION_NAME = "1.0.1"
    }

    object SResult {
        const val VERSION_CODE = 103016
        const val VERSION_NAME = "1.3.16"
    }
}