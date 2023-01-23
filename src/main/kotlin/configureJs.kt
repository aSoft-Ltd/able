import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.io.File

internal val Project.DEFAULT_DEV_SERVER
    get() = KotlinWebpackConfig.DevServer(
        open = false,
        static = mutableListOf(file("build/processedResources/js/main").absolutePath)
    )

/**
 * @param testTimeout in milliseconds
 */
fun Project.createKarmaTimeoutFile(testTimeout: Int) {
    val karmaDir = file("karma.config.d").apply { mkdirs() }
    File(karmaDir, "timeout.js").apply {
        createNewFile()
        writeText(
            """
            config.set({
              "client": {
                "mocha": {
                  "timeout": $testTimeout
                },
              },
              "browserDisconnectTimeout": $testTimeout
            });
        """.trimIndent()
        )
    }
}

/**
 * @param timeout in milliseconds
 */
fun KotlinJsTargetDsl.enableTesting(timeout: Int = 10000, forBrowser: Boolean = true, forNodeJs: Boolean = true) {
    project.createKarmaTimeoutFile(timeout)
    if (forBrowser) browser {}
    if (forNodeJs) nodejs {
        testTask {
            useMocha {
                this.timeout = "${timeout}ms"
            }
        }
    }
}

/**
 * @param testTimeout if null, testing is disabled
 */
fun KotlinJsTargetDsl.application(testTimeout: Int? = null, withNodeJs: Boolean = false) {
    browser {
        commonWebpackConfig {
            cssSupport.enabled = true
            outputFileName = "main.bundle.js"
            devServer = project.DEFAULT_DEV_SERVER
        }
    }
    binaries.executable()
    if (testTimeout != null) enableTesting(testTimeout, withNodeJs)
}

