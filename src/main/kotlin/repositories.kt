import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.publicRepos() {
//    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenCentral()
    google()
//    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
//    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") {
//        content { includeGroup("org.jetbrains.kotlin-wrappers") }
//    }
}
