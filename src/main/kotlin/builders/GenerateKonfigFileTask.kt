package builders

import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import java.io.File

open class GenerateKonfigFileTask : DefaultTask() {
    companion object {
        const val DEFAULT_KONFIG_FILE_NAME = "applikation.konfig"
        fun defaultFolderLocation(project: Project, konfig: Konfig, mppTarget: KotlinTarget?): File {
            val build = project.buildDir.absolutePath
            return when {
                project.plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> "$build/resources/main"
                project.plugins.hasPlugin("org.jetbrains.kotlin.js") -> "$build/resources/main"
                project.plugins.hasPlugin("org.jetbrains.kotlin.android") -> "$build/intermediates/konfigs"//"$build/intermediates/merged_assets/${konfig.name}/out"
                project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> when (mppTarget) {
                    is KotlinAndroidTarget -> "$build/intermediates/konfigs"//"$build/intermediates/merged_assets/${konfig.name}/out"
                    is KotlinJvmTarget -> "$build/processedResources/${mppTarget.name}/main"
                    is KotlinJsTarget -> "$build/resources/${mppTarget.name}"
                    is KotlinJsIrTarget -> "$build/resources/${mppTarget.name}"
                    else -> "$build/konfig/unsupported"
                }
                else -> "$build/konfig/unsupported"
            }.let { File(it).apply { mkdirs() } }
        }
    }

    init {
        group = "konfig"
    }

    @Internal
    var konfig = Konfig("default", Konfig.Type.DEBUG, mapOf("name" to "default"))

    @Internal
    var mppTarget: KotlinTarget? = null

    @get:InputDirectory
    val konfigDir: File
        get() = File(project.buildDir, "konfigs").apply { mkdirs() }

    private val konfigFile
        get() = File(konfigDir, "$DEFAULT_KONFIG_FILE_NAME.${konfig.name}.json")

    @get:OutputDirectory
    val outputDir: File
        get() = defaultFolderLocation(project, konfig, mppTarget)

    @get:OutputFile
    val outputFile: File
        get() = File(outputDir, "$DEFAULT_KONFIG_FILE_NAME.json")

    private val Konfig.json get() = JsonBuilder(values + ("name" to name)).toPrettyString()

    private fun File.readJson(): String? {
        if (!canRead()) return null
        return readText()
    }

    fun generate() {
        if (konfigFile.readJson() != konfig.json) konfigFile.writeText(konfig.json)
    }

    @TaskAction
    fun doAction() {
        project.copy {
            from(konfigFile) { rename { "$DEFAULT_KONFIG_FILE_NAME.json" } }
            into(outputDir)
        }
    }
}