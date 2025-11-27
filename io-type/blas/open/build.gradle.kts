plugins {
    kotlin("multiplatform")
}

// OS検出
val currentOs: String = System.getProperty("os.name").lowercase().let { osName ->
    when {
        osName.contains("mac") -> "macos"
        osName.contains("linux") -> "linux"
        osName.contains("win") -> "mingw"
        else -> error("Unsupported OS: $osName")
    }
}

// アーキテクチャ検出
val currentArch: String = System.getProperty("os.arch").lowercase().let { arch ->
    when (arch) {
        "amd64", "x86_64", "x86-64", "x64" -> "X64"
        "arm64", "aarch64", "armv8" -> "Arm64"
        else -> error("Unsupported architecture: $arch")
    }
}

// ライブラリのファイル名（OS依存）
val libFileName: String = when (currentOs) {
    "macos" -> "libopen_blas.dylib"
    "linux" -> "libopen_blas.so"
    "mingw" -> "open_blas.dll"
    else -> error("Unsupported OS: $currentOs")
}

val nativeLibFile = projectDir.resolve("cpp/build/$libFileName")
val jvmResourcesDir = projectDir.resolve("src/jvmMain/resources/open/$currentOs/$currentArch")

val copyNativeToResources by tasks.registering(Copy::class) {
    group = "build"
    description = "Copy native library to JVM resources for current platform"

    dependsOn(":io-type:blas:cpp:cmakeBuild")
    from(nativeLibFile)
    into(jvmResourcesDir)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutine)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            groupId = libs.versions.lib.group.id.get()
            artifactId = "perceptron"
            version = libs.versions.lib.version.get()
        }
    }
}
