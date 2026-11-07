import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.ksp)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    android {
        compileSdk = libs.versions.android.compile.sdk.get().toInt()
        minSdk = libs.versions.android.min.sdk.get().toInt()
        namespace = "com.wsr.knist.iotype"
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
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                api(projects.buffer)

                implementation(libs.serialization)
                compileOnly(projects.ioType.scopeAnnotation)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }
}

dependencies {
    kspCommonMainMetadata(projects.ioType.scopeProcessor)
}

tasks.withType<AbstractKotlinCompile<*>>().configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}
tasks.withType<KotlinNativeCompile>().configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}
tasks.matching { it.name.contains("CommonMainSourceSet") }.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}
tasks.matching { it.name.contains("SourcesJar", ignoreCase = true) }.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
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
