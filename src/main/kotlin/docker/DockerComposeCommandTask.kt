package docker

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class DockerComposeCommandTask @Inject constructor(
    private val composeFileTask: DockerComposeFileTask,
    private val commands: List<String>
) : DefaultTask() {
    init {
        group = "docker"
    }

    @TaskAction
    fun execute() {
        project.exec {
            workingDir(composeFileTask.outputDir)
            commandLine(commands)
        }
    }
}