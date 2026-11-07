import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.android.kmp.library)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    android {
        compileSdk = libs.versions.android.compile.sdk.get().toInt()
        minSdk = libs.versions.android.min.sdk.get().toInt()
        namespace = "com.wsr.knist.network"
        withHostTestBuilder { sourceSetTreeName = "test" }
    }

    val hostOs = DefaultNativePlatform.getCurrentOperatingSystem()
    val hostArch = DefaultNativePlatform.getCurrentArchitecture()
    when {
        hostOs.isMacOsX && hostArch.isAmd64 -> {
            macosX64()
            iosX64()
        }

        hostOs.isMacOsX && hostArch.isArm64 -> {
            macosArm64()
            iosArm64()
            iosSimulatorArm64()
        }

        hostOs.isLinux && hostArch.isAmd64 -> linuxX64()

        hostOs.isLinux && hostArch.isArm64 -> linuxArm64()

        hostOs.isWindows && hostArch.isAmd64 -> mingwX64()

        else -> throw GradleException("$hostOs:$hostArch is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.ioType)

                implementation(libs.serialization)
                implementation(libs.serialization.okio)
                implementation(libs.serialization.cbor)

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

    compilerOptions {
        freeCompilerArgs.addAll("-opt-in=kotlin.uuid.ExperimentalUuidApi")
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
