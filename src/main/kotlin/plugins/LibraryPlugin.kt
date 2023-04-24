package plugins

import com.android.build.gradle.LibraryExtension
import configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories
import publicRepos
import tasks.UpdateReadeMeTask
import java.io.File

open class LibraryPlugin : Plugin<Project> {
    fun Project.setupAndroidLib(dir: String) {
        configure<LibraryExtension> {
            configureAndroid(dir)
        }
    }

    private fun Project.setupUpdateReadMeTask() {
        val root = rootProject
        val v = root.version.toString()
        if (root.tasks.findByName("updateReadMe") == null) {
            root.tasks.register("updateReadMe", UpdateReadeMeTask::class.java).configure {
                configure(v)
            }
        }
        tasks.register("updateReadMe", UpdateReadeMeTask::class.java).configure {
            configure(v)
        }
    }

    override fun apply(project: Project) = with(project) {
        when {
            plugins.hasPlugin("com.android.library") -> when {
                plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> setupAndroidLib("src/androidMain")
                plugins.hasPlugin("org.jetbrains.kotlin.android") -> setupAndroidLib("src/main")
                else -> throw Throwable("You have added com.android.library plugin without specifying either kotlin-android or kotlin-mpp plugin")
            }
        }

        plugins.apply("maven-publish")
        project.afterEvaluate {
            setupUpdateReadMeTask()
        }
        repositories { publicRepos() }
    }
}