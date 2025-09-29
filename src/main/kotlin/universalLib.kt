import org.gradle.api.Action
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBrowserDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsNodeDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmWasiTargetDsl
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

fun KotlinAndroidTarget.library(java: String = "11", jupiter: Boolean = true) {
    targetJava(java, jupiter)
    publishLibraryVariants("release")
}

fun KotlinAndroidTarget.application(java: String = "11", jupiter: Boolean = true) {
    targetJava(java, jupiter)
}

fun KotlinJvmTarget.library(java: String = "11", jupiter: Boolean = true) {
    targetJava(java, jupiter)
}

//fun KotlinWithJavaTarget<KotlinJvmOptions, KotlinJvmCompilerOptions>.library(java: String = "11", jupiter: Boolean = true) {
//    targetJava(java, jupiter)
//}

//fun KotlinWithJavaTarget<KotlinJvmOptions, KotlinJvmCompilerOptions>.application(java: String = "11", jupiter: Boolean = true) {
//    targetJava(java, jupiter)
//}

fun KotlinJvmTarget.application(java: String = "11", jupiter: Boolean = true) {
    targetJava(java, jupiter)
}

/**
 * @param testTimeout in milliseconds, set to null to disable testing
 */
fun KotlinJsTargetDsl.browserLib(testTimeout: Int? = null, config: (KotlinJsTargetDsl.() -> Unit)? = null) {
    outputModuleName.set(project.name)
    browser()
    if (testTimeout != null) enableTesting(testTimeout, forBrowser = true, forNodeJs = false)
    if (config != null) config()
    compilerOptions {
        target.set("es2015")
        freeCompilerArgs.add("-Xes-long-as-bigint")
    }
}

/**
 * @param testTimeout in milliseconds, set to null to disable testing
 */
fun KotlinJsTargetDsl.browserApp(testTimeout: Int? = null, config: (KotlinJsBrowserDsl.() -> Unit)? = null) {
    outputModuleName.set(project.name)
    browserLib(testTimeout) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                outputFileName = "main.bundle.js"
                devServer = project.DEFAULT_DEV_SERVER
            }
            if (config != null) config()
        }
    }
    binaries.executable()
}

/**
 * @param testTimeout in milliseconds, set to null to disable testing
 */
fun KotlinJsTargetDsl.nodeLib(testTimeout: Int? = null, config: (KotlinJsNodeDsl.() -> Unit)? = null) {
    outputModuleName.set(project.name)
    nodejs(Action {
        testTask {
            if (config != null) config()
            if (testTimeout != null) useMocha {
                this.timeout = "${testTimeout}ms"
            } else enabled = false
        }
    })
}

fun KotlinJsTargetDsl.nodeApp(testTimeout: Int? = null, config: (KotlinJsNodeDsl.() -> Unit)? = null) {
    nodeLib(testTimeout, config)
    binaries.executable()
}


fun KotlinJsTargetDsl.library(testTimeout: Int? = null, config: (KotlinJsTargetDsl.() -> Unit)? = null) {
    outputModuleName.set(project.name)
    browser()
    nodejs()
    if (testTimeout != null) enableTesting(testTimeout, forBrowser = true, forNodeJs = true)
    if (config != null) config()

    compilerOptions {
        target.set("es2015")
        freeCompilerArgs.add("-Xes-long-as-bigint")
    }
}

fun KotlinWasmWasiTargetDsl.library(testTimeout: Int? = null, config: (KotlinWasmWasiTargetDsl.() -> Unit)? = null) {
    nodejs {
        if (testTimeout != null) testTask {
            useMocha {
                timeout = "${timeout}ms"
            }
        }
    }
    if (config != null) config()
}

//fun KotlinWasmTargetDsl.library(testTimeout: Int? = null, config: (KotlinJsTargetDsl.() -> Unit)? = null) {
//    moduleName = project.name
//    browser(Action {
//        commonWebpackConfig {
//            experiments = mutableSetOf("topLevelAwait")
//        }
//    })
////    nodejs()
//    if (testTimeout != null) enableTesting(testTimeout, forBrowser = true, forNodeJs = false)
//    if (config != null) config()
//}