package docker

import java.io.File

class DockerService(
    val name: String
) {
    internal val structure = mutableMapOf<String, Any>()
    fun build(context: File) {
        structure["build"] = mapOf(
            "context" to context.absolutePath
        )
    }

    fun build(value: String) {
        structure["build"] = value
    }

    fun ports(vararg ports: Pair<Int, Int>) {
        structure["ports"] = ports.map { "${it.first}:${it.second}" }
    }

    fun put(key: String, value: Any) {
        structure[key] = value
    }

    operator fun set(key: String, value: Any) {
        structure[key] = value
    }

    fun image(value: String) {
        structure["image"] = value
    }

    fun container(name: String) {
        structure["container_name"] = name
    }
}