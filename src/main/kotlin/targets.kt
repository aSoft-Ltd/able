import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.iosTargets() = listOf(
    iosArm64(),
    iosSimulatorArm64()
)

fun KotlinMultiplatformExtension.tvOsTargets() = listOf(
    tvosArm64(),
    tvosSimulatorArm64(),
)

fun KotlinMultiplatformExtension.watchOsTargets() = listOf(
    watchosSimulatorArm64(),
    watchosArm32(),
    watchosArm64(),

    // Deprecated
    watchosArm32()
)

fun KotlinMultiplatformExtension.macOsTargets() = listOf(
    //Tier 1
    macosX64(),
    macosArm64(),
)

fun KotlinMultiplatformExtension.osxTargets() = iosTargets() + tvOsTargets() + macOsTargets() + watchOsTargets()

fun KotlinMultiplatformExtension.osxComposeTargets() = listOf(
    iosArm64(),
    iosSimulatorArm64(),
    tvosArm64(),
    tvosSimulatorArm64(),
    macosArm64(),
    watchosSimulatorArm64(),
    watchosArm32(),
    watchosArm64(),

    // Deprecated
    watchosArm32()
)

fun KotlinMultiplatformExtension.linuxTargets() = listOf(
    linuxX64(),
)

fun KotlinMultiplatformExtension.ndkTargets() = listOf(
    androidNativeArm32(),
    androidNativeArm64(),
    androidNativeX86(),
    androidNativeX64(),
)

fun KotlinMultiplatformExtension.mingwTargets() = listOf(
    mingwX64()
)

fun KotlinMultiplatformExtension.nativeTargets() = osxTargets() + ndkTargets() + linuxTargets() + mingwTargets()