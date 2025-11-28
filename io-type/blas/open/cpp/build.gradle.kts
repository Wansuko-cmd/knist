// パスの定義
val cmakeSourceDir = projectDir
val cmakeBuildDir = projectDir.resolve("build")        // ビルド用一時ディレクトリ

// CMake 設定（cmake configure 相当）
val cmakeConfigure by tasks.registering(Exec::class) {
    group = "build"
    description = "Configure CMake for JNI native code"
    workingDir = cmakeSourceDir
    doFirst {
        cmakeBuildDir.mkdirs()
    }
    commandLine = listOf(
        "cmake",
        "-S", cmakeSourceDir.absolutePath,
        "-B", cmakeBuildDir.absolutePath,
        "-DCMAKE_BUILD_TYPE=Release",
    )
}

// CMake ビルド（make 相当）
val cmakeBuild by tasks.registering(Exec::class) {
    group = "build"
    description = "Build JNI native library via CMake"
    dependsOn(cmakeConfigure)
    workingDir = cmakeSourceDir
    commandLine = listOf(
        "cmake",
        "--build", cmakeBuildDir.absolutePath,
        "--config", "Release",
    )
}

// ===========================================
// Android向けビルドタスク
// ===========================================

// Android NDKのパスを取得（環境変数から）
val androidNdkHome: String? = System.getenv("ANDROID_NDK_HOME")

// Android ABI（環境変数またはプロパティから取得）
val androidAbi: String = System.getenv("ANDROID_ABI")
    ?: project.findProperty("androidAbi") as? String
    ?: "arm64-v8a"

// Android API Level
val androidPlatform: String = System.getenv("ANDROID_PLATFORM")
    ?: project.findProperty("androidPlatform") as? String
    ?: "android-24"

// CMake 設定（Android向け）
val cmakeConfigureAndroid by tasks.registering(Exec::class) {
    group = "build"
    description = "Configure CMake for Android native code"
    workingDir = cmakeSourceDir
    onlyIf { androidNdkHome != null }
    doFirst {
        cmakeBuildDir.mkdirs()
        if (androidNdkHome == null) {
            throw GradleException("ANDROID_NDK_HOME environment variable is not set")
        }
    }
    commandLine = listOf(
        "cmake",
        "-S", cmakeSourceDir.absolutePath,
        "-B", cmakeBuildDir.absolutePath,
        "-DCMAKE_BUILD_TYPE=Release",
        "-DCMAKE_TOOLCHAIN_FILE=$androidNdkHome/build/cmake/android.toolchain.cmake",
        "-DANDROID_ABI=$androidAbi",
        "-DANDROID_PLATFORM=$androidPlatform",
    )
}

// CMake ビルド（Android向け）
val cmakeBuildAndroid by tasks.registering(Exec::class) {
    group = "build"
    description = "Build JNI native library for Android via CMake"
    dependsOn(cmakeConfigureAndroid)
    workingDir = cmakeSourceDir
    onlyIf { androidNdkHome != null }
    commandLine = listOf(
        "cmake",
        "--build", cmakeBuildDir.absolutePath,
        "--config", "Release",
    )
}

// Cleanタスク
tasks.register<Delete>("clean") {
    group = "build"
    description = "Delete native build directory (excluding OpenBLAS)"
    delete(fileTree(projectDir.resolve("build")) {
        exclude("**/openblas/**")
    })
}
