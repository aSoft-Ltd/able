package docker

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File
import java.lang.StringBuilder

open class DockerComposeFileTask : DefaultTask() {

    init {
        group = "docker"
    }

    @Input
    val structure = mutableMapOf<String, Any>(
        "version" to "3.8"
    )

    @OutputDirectory
    var outputDir: File = File(project.buildDir, "docker").apply {
        if (!exists()) mkdirs()
    }

    @Input
    var outputFilename: String = "docker-compose.yml"

    fun StringBuilder.appendNested(level: Int, obj: Map<String, Any>, excludeBuild: Boolean) {
        for ((key, value) in obj) {
            if (excludeBuild && key == "build") continue
            append("  ".repeat(level))
            append("$key:")
            when (value) {
                is String, is Number -> append(" $value\n")
                is Map<*, *> -> {
                    append("\n")
                    appendNested(level + 1, value as Map<String, Any>, excludeBuild)
                }
                is List<*> -> {
                    for (items in value) {
                        append("\n")
                        append("  ".repeat(level + 1))
                        append("- $items")
                    }
                    append("\n")
                }
            }
        }
    }

    private fun formatted(map: Map<String, Any>, excludeBuild: Boolean) = buildString {
        append("version: ${map["version"]}\n\n")
        append("services:\n")
        val services = map["services"] as List<Map<String, Any>>
        for (service in services) {
            appendNested(level = 1, service, excludeBuild)
            if (service != services.lastOrNull()) append("\n")
        }
    }

    @get:Input
    var includeBuild: Boolean = false

    @TaskAction
    fun createFile() {
        val outputFile = File(outputDir, outputFilename)
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        outputFile.writeText(formatted(structure, excludeBuild = !includeBuild))
    }

    @get:Internal
    var version: Double
        get() = structure["version"].toString().toDouble()
        set(value) {
            version(value)
        }

    fun version(value: Double) {
        structure["version"] = """"$value""""
    }

    fun service(
        name: String,
        builder: DockerService.() -> Unit
    ) = DockerService(name).also {
        it.builder()
        val services = structure.getOrDefault("services", mutableListOf<Any>()) as MutableList<Any>
        services.add(mapOf(it.name to it.structure))
        structure["services"] = services
    }
}