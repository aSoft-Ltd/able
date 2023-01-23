package builders

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

class ApplikationKonfigJs(val project: Project, val konfig: Konfig, val mppTarget: KotlinTarget?) {
    init {
        with(project) {
            prepareWebpackConfigDir()
            createWebpackTasks()
            createRunTasks()
//            rootProject.plugins.withType(NodeJsRootPlugin::class.java) {
//                rootProject.the<NodeJsRootExtension>().versions.apply {
//                    webpackDevServer.version = "4.1.0"
//                    webpack.version = "5.51.1"
//                    webpackCli.version = "4.8.0"
//                }
//            }
        }
    }

    private fun prepareTaskName() = "prepare${mppTarget?.name?.capitalize() ?: ""}WebpackConfigDir"

    private fun Project.prepareWebpackConfigDir() {
        if (tasks.findByName(prepareTaskName()) != null) return
        tasks.create<PrepareWebpackConfigDirTask>(prepareTaskName()).also {
            it.mppTarget = mppTarget
        }
    }

    private fun Project.createWebpackTasks() = tasks.create<Copy>("webpack${mppTarget?.name?.capitalize() ?: ""}${konfig.name.capitalize()}") {
        group = "webpack"
        val productionTaskName = (if (mppTarget != null) mppTarget.name + "B" else "b") + "rowserProductionWebpack"
        dependsOn(prepareTaskName(), productionTaskName, konfig.generateKonfigFileTaskName(mppTarget))

        tasks.withType(KotlinJsIrLink::class.java).forEach {
            it.mustRunAfter(konfig.generateKonfigFileTaskName(mppTarget))
        }

        tasks.getByName("build").dependsOn(konfig.generateKonfigFileTaskName(mppTarget))

        from("build/distributions")
        if (mppTarget == null) {
            into("build/websites/${konfig.name}")
        } else {
            into("build/websites/${mppTarget.name}/${konfig.name}")
        }
        doLast {
            delete("build/distributions")
        }
    }

    private fun Project.createRunTasks() = tasks.create("run${mppTarget?.name?.capitalize() ?: ""}${konfig.name.capitalize()}") {
        group = "run"
        dependsOn(prepareTaskName(), konfig.generateKonfigFileTaskName(mppTarget))
        val prefix = (if (mppTarget != null) mppTarget.name + "B" else "b") + "rowser"
        tasks.withType(KotlinJsIrLink::class.java).forEach {
            it.mustRunAfter(konfig.generateKonfigFileTaskName(mppTarget))
        }
        if (konfig.type == Konfig.Type.DEBUG) {
            finalizedBy("${prefix}DevelopmentRun")
        } else {
            finalizedBy("${prefix}ProductionRun")
        }
    }
}