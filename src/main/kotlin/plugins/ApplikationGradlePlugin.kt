package plugins

import builders.ApplikationExtension
import builders.ApplikationKonfigAndroid
import builders.ApplikationKonfigJs
import builders.ApplikationKonfigJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import publicRepos

open class ApplikationGradlePlugin : Plugin<Project> {
    private fun Project.applyJvmConfiguration() {
        plugins.apply("application")
        afterEvaluate {
            extensions.findByType<ApplikationExtension>()?.konfigs?.forEach {
                ApplikationKonfigJvm(project, it, null)
            }
        }
    }

    private fun Project.applyJsConfiguration() = afterEvaluate {
        extensions.findByType<ApplikationExtension>()?.konfigs?.forEach {
            ApplikationKonfigJs(project, it, null)
        }
    }

    private fun Project.applyAndroidConfiguration() = afterEvaluate {
        extensions.findByType<ApplikationExtension>()?.konfigs?.forEach {
            ApplikationKonfigAndroid(project, it, null)
        }
    }

    private fun Project.applyMultiplatformConfiguration() = afterEvaluate {
        extensions.findByType<KotlinMultiplatformExtension>()?.targets?.forEach { target ->
            val konfigs =
                extensions.findByType<ApplikationExtension>()?.konfigs ?: return@forEach
            when (target) {
                is KotlinAndroidTarget -> konfigs.forEach { konfig ->
                    ApplikationKonfigAndroid(project, konfig, target)
                }

                is KotlinJvmTarget -> konfigs.forEach { konfig ->
                    ApplikationKonfigJvm(project, konfig, target)
                }

                is KotlinJsTarget -> konfigs.forEach { konfig ->
                    ApplikationKonfigJs(project, konfig, target)
                }

                is KotlinJsIrTarget -> konfigs.forEach { konfig ->
                    ApplikationKonfigJs(project, konfig, target)
                }
            }
        }
    }

    override fun apply(project: Project) = with(project) {
        val ext = extensions.create<ApplikationExtension>("applikation", project)
        project.afterEvaluate {
            println("Configuring ${ext.konfigs.map { it.name }} applikations")
        }
        project.repositories { publicRepos() }
        when {
            plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> applyJvmConfiguration()
            plugins.hasPlugin("org.jetbrains.kotlin.js") -> applyJsConfiguration()
            plugins.hasPlugin("org.jetbrains.kotlin.android") -> applyAndroidConfiguration()
            plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> applyMultiplatformConfiguration()
        }
    }
}