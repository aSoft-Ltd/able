package docker

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class DockerStackDeployTask @Inject constructor(
    private val composeFileTask: DockerComposeFileTask
) : DefaultTask() {
    init {
        group = "docker"
    }

    @Input
    var version = project.version.toString()

    @Input
    var stack: String = "${project.name}-$version"

    @Input
    var remote: String? = null

    @Input
    var username: String = ""

    @Input
    var password: String = ""

    @Input
    var destinationDir: String = ""

    private fun executeRemotely(remoteAddress: String) {
        project.exec {
            workingDir(composeFileTask.outputDir)
            commandLine(
                "scp",
                File(composeFileTask.outputDir, composeFileTask.outputFilename).absolutePath,
                "$username@$remoteAddress:$destinationDir/${version}/docker-compose.yml"
            )
        }
    }

    private fun executeLocally() {
        project.exec {
            workingDir(composeFileTask.outputDir)
            commandLine(
                "docker", "stack", "deploy", "-c", composeFileTask.outputFilename, stack
            )
        }
    }

    @TaskAction
    fun execute() {
        when (val remoteAddress = remote) {
            null -> executeLocally()
            else -> executeRemotely(remoteAddress)
        }
    }
}