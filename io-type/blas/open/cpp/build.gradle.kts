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

// Cleanタスク
tasks.register<Delete>("clean") {
    group = "build"
    description = "Delete native build directory (excluding OpenBLAS)"
    delete(fileTree(projectDir.resolve("build")) {
        exclude("**/openblas/**")
    })
}
