import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

private val projectDetails = mutableMapOf<Project, MutableMap<String, Any?>>()
var Project.url: String?
    get() = projectDetails[this]?.get("url") as? String
    set(value) {
        KotlinMultiplatformPlugin
        val map = projectDetails[this] ?: mutableMapOf()
        map["url"] = value
        projectDetails[this] = map
    }

var Project.scmUrl: String?
    get() = (projectDetails[this]?.get("scmUrl") as? String) ?: url
    set(value) {
        val map = projectDetails[this] ?: mutableMapOf()
        map["scmUrl"] = value
        projectDetails[this] = map
    }

var Project.scmConnection: String?
    get() = projectDetails[this]?.get("scmConnection") as? String
    set(value) {
        val map = projectDetails[this] ?: mutableMapOf()
        map["scmConnection"] = value
        projectDetails[this] = map
    }

var Project.scmDeveloperConnection: String?
    get() = projectDetails[this]?.get("scmDeveloperConnection") as? String
    set(value) {
        val map = projectDetails[this] ?: mutableMapOf()
        map["scmDeveloperConnection"] = value
        projectDetails[this] = map
    }

var Project.licenseName: String?
    get() = (projectDetails[this]?.get("licenseName") as? String) ?: "MIT License"
    set(value) {
        val map = projectDetails[this] ?: mutableMapOf()
        map["licenseName"] = value
        projectDetails[this] = map
    }

var Project.licenseUrl: String?
    get() =projectDetails[this]?.get("licenseUrl") as? String
    set(value) {
        val map = projectDetails[this] ?: mutableMapOf()
        map["licenseUrl"] = value
        projectDetails[this] = map
    }

var Project.developerId: String?
    get() = projectDetails[this]?.get("developerId") as? String
    set(value) {
        val map = projectDetails[this] ?: mutableMapOf()
        map["developerId"] = value
        projectDetails[this] = map
    }

var Project.developerName: String?
    get() = projectDetails[this]?.get("developerName") as? String
    set(value) {
        val map = projectDetails[this] ?: mutableMapOf()
        map["developerName"] = value
        projectDetails[this] = map
    }