import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import java.io.File

fun Project.configurePublishing(config: PublishingExtension.() -> Unit) {
    val android = extensions.findByType<BaseExtension>()

    /**
     * Javadoc
     */
    var javadocTask = tasks.findByName("javadoc") as Javadoc?

    if (javadocTask == null && android != null) {
        // create the Android javadoc if needed
        javadocTask = tasks.create<Javadoc>("javadoc") {
            source = android.sourceSets["main"].java.getSourceFiles()
            classpath += project.files(android.bootClasspath.joinToString(File.pathSeparator))

            (android as? LibraryExtension)?.libraryVariants?.configureEach {
                if (name != "release") {
                    return@configureEach
                }
                classpath += getCompileClasspath(null)
            }
        }
    } else if (javadocTask == null) {
        javadocTask = tasks.create<Javadoc>("javadoc")
    }

    val javadocJarTaskProvider: TaskProvider<Jar> = tasks.register<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        dependsOn(javadocTask)
        from(javadocTask.destinationDir)
    }

    configure<PublishingExtension> {
        publications {
            when {
                plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
                    withType<MavenPublication> {
                        artifact(javadocJarTaskProvider.get())
                    }
                }

                plugins.hasPlugin("org.jetbrains.kotlin.android") -> afterEvaluate {
                    val sourcesJarTaskProvider = tasks.register<Jar>("sourcesJar") {
                        archiveClassifier.set("sources")
                        from(android!!.sourceSets["main"].java.srcDirs)
                    }
                    create<MavenPublication>("main") {
                        groupId = this@configurePublishing.group.toString()
                        artifactId = this@configurePublishing.name
                        version = this@configurePublishing.version.toString()
                        from(components["release"])
                        artifact(javadocJarTaskProvider.get())
                        artifact(sourcesJarTaskProvider.get())
                    }
                }

                plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> {
                    create<MavenPublication>("main") {
                        groupId = this@configurePublishing.group.toString()
                        artifactId = this@configurePublishing.name
                        version = this@configurePublishing.version.toString()
                        from(components["kotlin"])
                        artifact(javadocJarTaskProvider.get())
                        artifact(tasks.findByName("kotlinSourcesJar"))
                    }
                }

                plugins.hasPlugin("org.jetbrains.kotlin.js") -> {
                    create<MavenPublication>("main") {
                        groupId = this@configurePublishing.group.toString()
                        artifactId = this@configurePublishing.name
                        version = this@configurePublishing.version.toString()
                        from(components["kotlin"])
                        artifact(javadocJarTaskProvider.get())
                        artifact(tasks.findByName("jsSourcesJar"))
                    }
                }
            }

            withType<MavenPublication> {
                setDefaultPomFields(this)
            }
        }
        config()
    }
}

fun Project.setDefaultPomFields(mavenPublication: MavenPublication) {
    mavenPublication.groupId = group.toString()
    mavenPublication.version = version.toString()

    mavenPublication.pom {
        name.set(this@setDefaultPomFields.name)
        (findProperty("POM_PACKAGING") as String?)?.let {
            // Do not overwrite packaging if set by the multiplatform plugin
            packaging = it
        }

        description.set(this@setDefaultPomFields.description)
        url.set(this@setDefaultPomFields.url)

        scm {
            url.set(this@setDefaultPomFields.scmUrl)
            connection.set(this@setDefaultPomFields.scmConnection)
            developerConnection.set(this@setDefaultPomFields.scmDeveloperConnection)
        }

        licenses {
            license {
                name.set(this@setDefaultPomFields.licenseName)
                url.set(this@setDefaultPomFields.licenseUrl)
            }
        }

        developers {
            developer {
                id.set(this@setDefaultPomFields.developerId)
                name.set(this@setDefaultPomFields.developerName)
            }
        }
    }
}