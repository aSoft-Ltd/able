package plugins

import com.android.build.gradle.LibraryExtension
import configureAndroid
import enableTesting
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import publicRepos

open class LibraryPlugin : Plugin<Project> {
    fun Project.setupAndroidLib(dir: String) {
        configure<LibraryExtension> {
            configureAndroid(dir)
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

        repositories { publicRepos() }
    }
}