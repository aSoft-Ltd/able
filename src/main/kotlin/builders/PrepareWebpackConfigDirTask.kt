package builders

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File

open class PrepareWebpackConfigDirTask : DefaultTask() {
    @OutputDirectory
    var configDir: File = File(project.projectDir, "webpack.config.d").apply {
        if (!exists()) mkdirs()
    }

    @Internal
    var mppTarget: KotlinTarget? = null

    @Input
    var outputFilename: String = "konfig.js"

    @get:OutputFile
    val outputFile: File
        get() = File(configDir, outputFilename).apply {
            if (!exists()) createNewFile()
        }

    @TaskAction
    fun prepare() {
        outputFile.apply {
            writeText("""config.resolve.modules.push("${project.buildDir.absolutePath}/resources/${mppTarget?.name ?: "main"}")""")
            appendText("\n")
            appendText(
                """
                config.module.rules.push({
                    test: /\.(png|jpe?g|gif|svg)${'$'}/i,
                    use: [
                      {
                        loader: 'file-loader',
                      },
                    ],
                });
                config.devServer = { ...config.devServer, historyApiFallback: true, host: "0.0.0.0" }
            """.trimIndent()
            )
        }
    }
}