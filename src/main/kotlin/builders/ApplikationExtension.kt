package builders

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

open class ApplikationExtension(val project: Project) {
    private var kommon = Konfig("common", Konfig.Type.RELEASE, mapOf())
    internal val konfigs = mutableListOf<Konfig>()

    @OptIn(ExperimentalStdlibApi::class)
    internal fun konfig(name: String, type: Konfig.Type, vararg properties: Pair<String, Any>) {
        if (konfigs.any { it.name.contains(name, ignoreCase = true) }) {
            error("Konfig $name already exists")
        }
        Konfig(name, type, buildMap {
            put("name", name)
            putAll(kommon.values)
            putAll(properties)
            val company = project.group.toString()
            put("company", company)
            put("namespace", "$company.$name")
            put("version", project.version.toString())
        }).also { konfig ->
            konfigs.add(konfig)
            val ext = project.extensions.findByType(KotlinMultiplatformExtension::class.java)
            if (ext == null) {
                project.tasks.create<GenerateKonfigFileTask>("generate${konfig.name.capitalize()}KonfigFile") {
                    this.konfig = konfig
                }.generate()
            } else {
                project.afterEvaluate {
                    ext.targets.filter { target ->
                        target is KotlinAndroidTarget || target is KotlinJvmTarget || target is KotlinJsIrTarget
                    }.forEach { target ->
                        tasks.create<GenerateKonfigFileTask>("generate${target.name.capitalize()}${konfig.name.capitalize()}KonfigFile") {
                            this.konfig = konfig
                            mppTarget = target
                        }.generate()
                    }
                }
            }
        }
    }

    fun common(vararg properties: Pair<String, Any>) {
        kommon = Konfig("common", Konfig.Type.RELEASE, kommon.values + properties)
    }

    fun debug(name: String, vararg properties: Pair<String, Any>) = konfig(name, Konfig.Type.DEBUG, *properties)
    fun debug(vararg properties: Pair<String, Any>) = debug("debug", *properties)
    fun staging(name: String, vararg properties: Pair<String, Any>) = konfig(name, Konfig.Type.STAGING, *properties)
    fun staging(vararg properties: Pair<String, Any>) = staging("staging", *properties)
    fun release(name: String, vararg properties: Pair<String, Any>) = konfig(name, Konfig.Type.RELEASE, *properties)
    fun release(vararg properties: Pair<String, Any>) = release("release", *properties)
}