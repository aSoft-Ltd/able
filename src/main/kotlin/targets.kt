import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalStdlibApi::class)
fun KotlinMultiplatformExtension.iosTargets(supportedByCoroutines: Boolean) = buildList {
    add(iosArm64())
    add(iosArm32())
    add(iosX64())
}

@OptIn(ExperimentalStdlibApi::class)
fun KotlinMultiplatformExtension.tvOsTargets(supportedByCoroutines: Boolean) = buildList {
    add(tvosArm64())
    add(tvosX64())
}

fun KotlinMultiplatformExtension.watchOsTargets(supportedByCoroutines: Boolean) = listOf(
    watchosX64(),
    watchosX86(),
    watchosArm32(),
    watchosArm64()
)

fun KotlinMultiplatformExtension.macOsTargets(supportedByCoroutines: Boolean) = listOf(
    macosX64()
)

@OptIn(ExperimentalStdlibApi::class)
fun KotlinMultiplatformExtension.darwinTargets(supportedByCoroutines: Boolean) = buildList {
    addAll(iosTargets(supportedByCoroutines))
    addAll(tvOsTargets(supportedByCoroutines))
    addAll(macOsTargets(supportedByCoroutines))
    addAll(watchOsTargets(supportedByCoroutines))
}

@OptIn(ExperimentalStdlibApi::class)
fun KotlinMultiplatformExtension.linuxTargets(supportedByCoroutines: Boolean) = buildList {
    add(linuxX64())
    if (!supportedByCoroutines) {
        add(linuxArm64())
        add(linuxArm32Hfp())
    }
}

fun KotlinMultiplatformExtension.nativeTargets(
    supportedByCoroutines: Boolean
) = darwinTargets(supportedByCoroutines) + linuxTargets(supportedByCoroutines)