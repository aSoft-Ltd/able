@Suppress("DSL_SCOPE_VIOLATION") plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    signing
    alias(plugs.plugins.nexus.publish)
    alias(plugs.plugins.publish)
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    plugins {
        val applikation by creating {
            id = "tz.co.asoft.applikation"
            implementationClass = "plugins.ApplikationGradlePlugin"
        }

        val deploy by creating {
            id = "tz.co.asoft.deploy"
            description = "A gradle extension to deploy to sonatype"
            implementationClass = "plugins.DeployToSonatypePlugin"
        }

        val library by creating {
            id = "tz.co.asoft.library"
            description = "A kotlin library plugin"
            implementationClass = "plugins.LibraryPlugin"
        }
    }
}

group = "tz.co.asoft"
version = asoft.versions.foundation.get()

pluginBundle {
    website = "https://github.com/aSoft-Ltd/foundation/tree/master/foundation-plugins"
    vcsUrl = website
    description = "Simple Plugins to Ease Library Development"

    mavenCoordinates {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
    }

    plugins {
        val applikation by getting {
            displayName = "Applikation Plugin"
            tags = listOf("kotlin", "application", "frontend")
        }

        val deploy by getting {
            displayName = "Deploy Plugin"
            tags = listOf("asoft", "nexus", "deploy")
        }

        val library by getting {
            displayName = "Library Plugin"
            tags = listOf("kotlin", "library")
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("ASOFT_NEXUS_USERNAME"))
            password.set(System.getenv("ASOFT_NEXUS_PASSWORD"))
        }
    }
}

val pgpPrivateKey = System.getenv("ASOFT_MAVEN_PGP_PRIVATE_KEY") ?: ""
val pgpPassword = System.getenv("ASOFT_MAVEN_PGP_PASSWORD") ?: ""

signing {
    useInMemoryPgpKeys(pgpPrivateKey, pgpPassword)
    val publicationsContainer = (extensions["publishing"] as PublishingExtension).publications
    sign(publicationsContainer)
}


defaultTasks("jar")

val sourcesJar by tasks.creating(org.gradle.jvm.tasks.Jar::class) {
    archiveClassifier.value("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.creating(org.gradle.jvm.tasks.Jar::class) {
    archiveClassifier.value("javadoc")
}

publishing {
    repositories {
        maven {
            name = "buildDir"
            url = buildDir.resolve("maven").toURI()
        }
    }

    publications {
        afterEvaluate {
            withType<MavenPublication> {
                artifact(sourcesJar)
                artifact(javadocJar)
                pom {
                    name.set("Foundation Plugins - ${this@withType.name}")
                    (findProperty("POM_PACKAGING") as String?)?.let {
                        // Do not overwrite packaging if set by the multiplatform plugin
                        packaging = it
                    }
                    description.set("A collection of gradle plugins to ease library development")
                    url.set("https://github.com/aSoft-Ltd/foundation/tree/master/foundation-plugins")
                    scm {
                        url.set("https://github.com/aSoft-Ltd/foundation/tree/master/foundation-plugins")
                        connection.set("scm:git:git://github.com/aSoft-Ltd/foundation-plugins.git")
                        developerConnection.set("scm:git:https://github.com/aSoft-Ltd/foundation-plugins.git")
                    }
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/aSoft-Ltd/foundation-plugins/blob/master/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("andylamax")
                            name.set("Anderson Lameck")
                        }
                    }
                }
            }
        }
    }
}

artifacts {
    archives(sourcesJar)
}

dependencies {
    implementation(plugs.android)
    implementation(plugs.kotlin.core)
//    implementation(plugs.kotlin.serialization)
    implementation(plugs.nexus.staging)
    implementation(plugs.nexus.publish)
}
