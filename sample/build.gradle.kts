@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm {
        mainRun { mainClass = "MainKt" }
        testRuns.named("test") {
            executionTask.configure {
                minHeapSize = "256M"
                maxHeapSize = "${1024 * 6}M"
                jvmArgs("-XX:MaxMetaspaceSize=1024M")
            }
        }
    }

    val hostOs = DefaultNativePlatform.getCurrentOperatingSystem()
    val hostArch = DefaultNativePlatform.getCurrentArchitecture()
    val hostTarget = when {
        hostOs.isMacOsX && hostArch.isAmd64 -> macosX64()
        hostOs.isMacOsX && hostArch.isArm64 -> macosArm64()
        hostOs.isLinux && hostArch.isAmd64 -> linuxX64()
        hostOs.isLinux && hostArch.isArm64 -> linuxArm64()
        hostOs.isWindows && hostArch.isAmd64 -> mingwX64()
        else -> throw GradleException("$hostOs:$hostArch is not supported in Kotlin/Native.")
    }

    hostTarget.binaries {
        executable {
            entryPoint = "main"
        }
    }

    targets.withType<KotlinNativeTargetWithHostTests>().configureEach {
        binaries.test(listOf(NativeBuildType.RELEASE))
        testRuns.getByName("test") {
            setExecutionSourceFrom(binaries.getTest(NativeBuildType.RELEASE))
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.network)
                implementation(projects.buffer.gpu)

                implementation(libs.serialization)
                implementation(libs.okio)
                implementation(libs.coroutines)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }
    }
}
