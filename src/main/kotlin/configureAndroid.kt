import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget

/**
 * Configures the android plugin - (both application and library)
 */

fun Project.configureAndroid(
    dir: String = "src/androidMain",
    builder: (BaseExtension.() -> Unit)? = null
) = configure<BaseExtension> {
    configureAndroid(dir)
    builder?.invoke(this)
}

fun BaseExtension.configureAndroid(dir: String = "src/androidMain") {

    compileSdkVersion(30)

    buildFeatures.apply {
        buildConfig = false
    }

    defaultConfig {
        minSdk = 18
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main") {
            java.srcDir("$dir/kotlin")
            manifest.srcFile("$dir/AndroidManifest.xml")
            res.srcDirs("$dir/resources")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            setMatchingFallbacks("release")
        }
    }

    lintOptions {
        isAbortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

fun KotlinAndroidTarget.targetJava(version: String = "1.8", jupiter: Boolean = true) = compilations.all {
    kotlinOptions {
        jvmTarget = version
        freeCompilerArgs = listOf("-Xallow-unstable-dependencies")
    }
    if (jupiter) project.useJunit5()
}