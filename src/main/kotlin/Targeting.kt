object Targeting {

    val ANDROID get() = targeting("ANDROID", true) || ALL

    val JVM get() = targeting("JVM", true) || ALL

    val JS get() = targeting("JS", true) || ALL

    val WASM get() = targeting("WASM", true) || ALL

    val OSX get() = targeting("OSX", false) || ALL_NATIVE

    val NDK get() = targeting("NDK", false) || ALL_NATIVE

    val LINUX get() = targeting("LINUX", true) || ALL_NATIVE

    val MINGW get() = targeting("MINGW", false) || ALL_NATIVE

    val SOME_NATIVE get() = LINUX || OSX || MINGW || NDK

    val ALL_NATIVE get() = targeting("NATIVE", false) || ALL

    val ALL get() = targeting("ALL", false)

    private fun targeting(key: String, default: Boolean) = when (System.getenv("TARGETING_$key")) {
        "true" -> true
        "false" -> false
        "" -> default
        null -> default
        else -> default
    }
}