import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinWithJavaTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

fun Project.useJunit5() {
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

fun KotlinJvmTarget.targetJava(version: String = "1.8", jupiter: Boolean = true) = compilations.all {
    compileJavaTaskProvider?.configure { targetJvm(version) }
    kotlinOptions {
        jvmTarget = version
    }
    if (jupiter) project.useJunit5()
}

fun KotlinWithJavaTarget<KotlinJvmOptions, KotlinJvmCompilerOptions>.targetJava(version: String = "1.8", jupiter: Boolean = true) = compilations.all {
    compileJavaTaskProvider.configure { targetJvm(version) }
    kotlinOptions {
        jvmTarget = version
    }
    if (jupiter) project.useJunit5()
}

fun JavaCompile.targetJvm(version: String) {
    sourceCompatibility = version
    targetCompatibility = version
}