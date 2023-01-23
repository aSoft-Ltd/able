import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinWithJavaTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

fun Project.useJunit5() {
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

fun KotlinJvmTarget.targetJava(version: String = "1.8") = compilations.all {
    kotlinOptions {
        jvmTarget = version
        freeCompilerArgs = listOf("-Xallow-unstable-dependencies")
    }
    project.useJunit5()
}

fun KotlinWithJavaTarget<KotlinJvmOptions>.targetJava(version: String = "1.8") {
    compilations.all {
        kotlinOptions {
            jvmTarget = version
            freeCompilerArgs = listOf("-Xallow-unstable-dependencies")
        }
    }
    project.useJunit5()
}