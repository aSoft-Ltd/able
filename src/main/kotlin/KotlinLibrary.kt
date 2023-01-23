import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

fun Project.KotlinLibrary(
    group: String,
    version: String,
    config: (PublishingExtension.() -> Unit) = {}
) {
    this.group = group
    this.version = version
    configurePublishing(config)
}