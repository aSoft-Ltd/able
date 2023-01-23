package builders

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget

class ApplikationKonfigAndroid(
    val project: Project,
    val konfig: Konfig,
    val mppTarget: KotlinAndroidTarget?
) {
    init {
        project.createPreBuildTasks(konfig)
        project.createInstallRunTasks(konfig)
    }

    private fun Project.createPreBuildTasks(kfg: Konfig) {
        tasks.getByName("pre${kfg.name.capitalize()}Build").apply {
            dependsOn(kfg.generateKonfigFileTaskName(mppTarget))
        }
    }

    private fun Project.createInstallRunTasks(konfig: Konfig) {
        val androidExt = extensions.findByType(BaseAppModuleExtension::class.java)
        val variants = androidExt?.applicationVariants
        val variant = variants?.find { it.name.equals(konfig.name, ignoreCase = true) } ?: return
        val installTask = tasks.findByName("install${konfig.name.capitalize()}") ?: return

        tasks.create<Exec>("installRun${mppTarget?.name?.capitalize() ?: ""}${konfig.name.capitalize()}") {
            group = "run"
            dependsOn(installTask)
            commandLine("adb", "shell", "monkey", "-p", variant.applicationId + " 1")
            doLast { println("Launching ${variant.applicationId}") }
        }
    }
}