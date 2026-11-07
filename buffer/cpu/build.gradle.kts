import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.kmp.library)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    android {
        compileSdk = libs.versions.android.compile.sdk.get().toInt()
        minSdk = libs.versions.android.min.sdk.get().toInt()
        namespace = "com.wsr.knist.cpu"
    }

    val hostOs = DefaultNativePlatform.getCurrentOperatingSystem()
    val hostArch = DefaultNativePlatform.getCurrentArchitecture()
    val hostTargets = when {
        hostOs.isMacOsX && hostArch.isAmd64 -> listOf(macosX64(), iosX64())
        hostOs.isMacOsX && hostArch.isArm64 -> listOf(macosArm64(), iosArm64(), iosSimulatorArm64())
        hostOs.isLinux && hostArch.isAmd64 -> listOf(linuxX64())
        hostOs.isLinux && hostArch.isArm64 -> listOf(linuxArm64())
        hostOs.isWindows && hostArch.isAmd64 -> listOf(mingwX64())
        else -> throw GradleException("$hostOs:$hostArch is not supported in Kotlin/Native.")
    }
    hostTargets.forEach { target ->
        target.compilations.getByName("main") {
            cinterops {
                val lib by creating {
                    val headersDir = "$projectDir/rs/src/bindings/native/headers"
                    headers(
                        "$headersDir/buffer_native.h",
                        "$headersDir/runtime_native.h",
                        "$headersDir/compare_native.h",
                        "$headersDir/generator_native.h",
                        "$headersDir/index_native.h",
                        "$headersDir/mat_mul_native.h",
                        "$headersDir/math_native.h",
                        "$headersDir/operation_native.h",
                        "$headersDir/reduction_native.h",
                        "$headersDir/shape_native.h",
                    )
                    defFile(project.file("src/nativeMain/lib.def"))
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.buffer.base)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmAndroidMain by creating {
            dependsOn(commonMain)
        }
        jvmMain {
            dependsOn(jvmAndroidMain)
        }
        androidMain {
            dependsOn(jvmAndroidMain)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

afterEvaluate {
    publishing {
        publications {
            withType<MavenPublication>().configureEach {
                groupId = libs.versions.lib.group.id.get()
                version = libs.versions.lib.version.get()
            }
        }
    }
}
