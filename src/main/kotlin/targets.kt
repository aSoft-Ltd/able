import linuxTargets
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.iosTargets() = listOf(
    // Tier 1
    iosX64(),

    // Tier 2
    iosArm64(),
    iosSimulatorArm64()
)

fun KotlinMultiplatformExtension.tvOsTargets() = listOf(
    // Tier 1
    // No tier 1 tvos targets
    // Tier 2
    tvosArm64(),
    tvosSimulatorArm64(),
    tvosX64()
)

fun KotlinMultiplatformExtension.watchOsTargets() = listOf(
    // No Tier 1 watchOsTargets at the moment
    // Tier 2
    watchosSimulatorArm64(),
    watchosX64(),
    watchosX86(),
    watchosArm32(),
    watchosArm64(),
    // Tier 3
    watchosDeviceArm64(),
)

fun KotlinMultiplatformExtension.macOsTargets() = listOf(
    //Tier 1
    macosX64(),
    macosArm64(),
)

fun KotlinMultiplatformExtension.osxTargets() = iosTargets() + tvOsTargets() + macOsTargets() + watchOsTargets()

fun KotlinMultiplatformExtension.linuxTargets() = listOf(
    // Tier 1
    linuxX64(),
    // Tier 2
    linuxArm64(),
)

fun KotlinMultiplatformExtension.ndkTargets() = listOf(
    // No Tier 1 and 2 ndk targets for now
    androidNativeArm32(),
    androidNativeArm64(),
    androidNativeX86(),
    androidNativeX64(),
)

fun KotlinMultiplatformExtension.mingwTargets() = listOf(
    // No Tier 1 and 2 mingw targets for now
    mingwX64(),
)

fun KotlinMultiplatformExtension.nativeTargets() = osxTargets() + ndkTargets() + linuxTargets() + mingwTargets()