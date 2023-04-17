import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinWithJavaTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBrowserDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsNodeDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

fun KotlinAndroidTarget.library(java: String = "1.8") {
    targetJava(java)
    publishLibraryVariants("release")
}

fun KotlinAndroidTarget.application(java: String = "1.8") {
    targetJava(java)
}

fun KotlinJvmTarget.library(java: String = "1.8") {
    targetJava(java)
}

fun KotlinWithJavaTarget<KotlinJvmOptions, KotlinJvmCompilerOptions>.library(java: String = "1.8") {
    targetJava(java)
}

fun KotlinWithJavaTarget<KotlinJvmOptions, KotlinJvmCompilerOptions>.application(java: String = "1.8") {
    targetJava(java)
}

fun KotlinJvmTarget.application(java: String = "1.8") {
    targetJava(java)
}

/**
 * @param testTimeout in milliseconds, set to null to disable testing
 */
fun KotlinJsTargetDsl.browserLib(testTimeout: Int? = null, config: (KotlinJsBrowserDsl.() -> Unit)? = null) {
    if (testTimeout != null) project.createKarmaTimeoutFile(testTimeout)
    compilations.all { kotlinOptions { sourceMap = true } }
    browser {
        commonWebpackConfig { sourceMaps = true }
        if (testTimeout == null) testTask { enabled = false }
        if (config != null) config()
    }
}

/**
 * @param testTimeout in milliseconds, set to null to disable testing
 */
fun KotlinJsTargetDsl.browserApp(testTimeout: Int? = null, config: (KotlinJsBrowserDsl.() -> Unit)? = null) {
    browserLib(testTimeout) {
        commonWebpackConfig {
            cssSupport {
                enabled.set(true)
            }
            outputFileName = "main.bundle.js"
            devServer = project.DEFAULT_DEV_SERVER
        }
        if (config != null) config()
    }
    binaries.executable()
}

/**
 * @param testTimeout in milliseconds, set to null to disable testing
 */
fun KotlinJsTargetDsl.nodeLib(testTimeout: Int? = null, config: (KotlinJsNodeDsl.() -> Unit)? = null) {
    nodejs {
        testTask {
            if (config != null) config()
            if (testTimeout != null) useMocha {
                this.timeout = "${testTimeout}ms"
            } else enabled = false
        }
    }
}

fun KotlinJsTargetDsl.nodeApp(testTimeout: Int? = null, config: (KotlinJsNodeDsl.() -> Unit)? = null) {
    nodeLib(testTimeout, config)
    binaries.executable()
}


fun KotlinJsTargetDsl.library(testTimeout: Int? = null, config: (KotlinJsTargetDsl.() -> Unit)? = null) {
    browser()
    nodejs()
    if (testTimeout != null) enableTesting(testTimeout, forBrowser = true, forNodeJs = true)
    if (config != null) config()
}

fun KotlinWasmTargetDsl.library(testTimeout: Int? = null, config: (KotlinJsTargetDsl.() -> Unit)? = null) {
    browser {
        commonWebpackConfig {
            experiments = mutableSetOf("topLevelAwait")
        }
    }
//    nodejs()
    if (testTimeout != null) enableTesting(testTimeout, forBrowser = true, forNodeJs = false)
    if (config != null) config()
}