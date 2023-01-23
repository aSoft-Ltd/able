import org.gradle.api.plugins.PluginContainer

internal fun PluginContainer.applyKotlin(platform: String) = apply("org.jetbrains.kotlin.$platform")