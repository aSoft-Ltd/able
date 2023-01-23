import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun KotlinDependencyHandler.asoft(module: String, version: String) = "tz.co.asoft:$module:$version"
fun KotlinDependencyHandler.kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"
fun KotlinDependencyHandler.wrapper(module: String, version: String) = "org.jetbrains:kotlin-$module:$version"
