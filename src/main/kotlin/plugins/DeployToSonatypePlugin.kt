package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import deployToSonatype
import deployers.DeployToSonatypeExtension

open class DeployToSonatypePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("deployToSonatype", DeployToSonatypeExtension::class.java, target)
        target.afterEvaluate {
            deployToSonatype(group = extension.group, version = extension.version)
        }
    }
}