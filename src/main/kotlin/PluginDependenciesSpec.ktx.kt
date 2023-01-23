import org.gradle.plugin.use.PluginDependenciesSpec

fun PluginDependenciesSpec.asoft(plugin: String) = id("tz.co.asoft.$plugin")