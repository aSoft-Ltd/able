package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class UpdateReadeMeTask : DefaultTask() {

    companion object {
        internal val DEFAULT_INPUT_PATH = "docs/ReadMe.md"
    }

    @get:InputFile
    @get:Optional
    abstract val input: RegularFileProperty

    @get:OutputFile
    abstract val output: RegularFileProperty

    private val projName = project.name

    @get:Input
    abstract val version: Property<String>

    internal fun configure(v: String) {
        val root = project.layout.projectDirectory
        val src = root.file(DEFAULT_INPUT_PATH)
        if (src.asFile.exists()) {
            input.convention(src)
        }
        version.convention(v)
        output.convention(root.file("ReadMe.md"))
    }

    @TaskAction
    fun update() {
        val file = srcFile()
        if (file == null) {
            logger.warn("ReadMe src file is missing")
            logger.warn("Path docs/ReadMe.md can't be found")
            return
        }
        val doc = file.readText()
            .replace("[badges]", badges())
            .replace("[version]", version.get())
            .injectIfPossible(file.parentFile)

        output.get().asFile.writeText(doc)
    }

    private fun String.locateTokens(): MutableMap<String, String> {
        val tokens = mutableMapOf<String, String>()
        val splits = split("[inject](")
        if (splits.size > 1) for (i in splits.indices) {
            val path = splits.getOrNull(i + 1)?.split(")")?.getOrNull(0)
            if (path != null) {
                tokens[path] = ""
            }
        }
        return tokens
    }

    private fun String.injectIfPossible(main: File): String {
        val tokens = locateTokens()
        val contents = tokens.mapValues { (path, _) ->
            File(main, path).readText()
        }
        var out = this
        contents.forEach { (path, content) ->
            out = out.replace("[inject]($path)", "\n```kotlin\n$content\n```\n")
        }
        return out
    }

    private fun srcFile() = if (input.isPresent) {
        input.asFile.get()
    } else null

    private fun badges(): String {
        return """
        ![Kotlin](https://img.shields.io/badge/kotlin-multiplatform-blue?style=for-the-badge&logo=kotlin&logoColor=white)
        ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
        ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=&logoColor=white)
        ![Swift](https://img.shields.io/badge/swift-F54A2A?style=for-the-badge&logo=swift&logoColor=white)
        ![iOS](https://img.shields.io/badge/iOS-000000?style=for-the-badge&logo=ios&logoColor=white)
        ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)
        ![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
    """.trimIndent()
    }
}