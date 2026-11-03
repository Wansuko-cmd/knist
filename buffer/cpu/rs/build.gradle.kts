val sourceDir = projectDir
val targetDir = projectDir.resolve("target")

/**
 * JVM
 **/
val cargoJvmBuild by tasks.registering(Exec::class) {
    group = "build"
    description = "JVM環境向けにビルド"
    workingDir = sourceDir
    commandLine = listOf("cargo", "build", "--release", "--no-default-features", "--features", "jni")
}

/**
 * Android
 **/
val cargoAndroidArm64Build by tasks.registering(Exec::class) {
    group = "build"
    description = "Android (arm64-v8a) 向けにビルド"
    workingDir = sourceDir
    commandLine = listOf("cargo", "build", "--release", "--target", "aarch64-linux-android", "--no-default-features", "--features", "jni")
}

val cargoAndroidX64Build by tasks.registering(Exec::class) {
    group = "build"
    description = "Android (x86_64) 向けにビルド"
    workingDir = sourceDir
    commandLine = listOf("cargo", "build", "--release", "--target", "x86_64-linux-android", "--no-default-features", "--features", "jni")
}

/**
 * Native
 **/
val target = providers.gradleProperty("target")

val cargoNativeBuild by tasks.registering(Exec::class) {
    group = "build"
    description = "Native環境向けにビルド"
    workingDir = sourceDir
    val targetArgs = target.map { listOf("--target", it) }.getOrElse(emptyList())
    commandLine = listOf("cargo", "build", "--release") + targetArgs + listOf("--no-default-features", "--features", "native")
}

val cargoNativeRename by tasks.registering(Exec::class) {
    group = "build"
    description = "Native用 staticlib の公開API以外のシンボルを改名する"
    dependsOn(cargoNativeBuild)

    val lib = targetDir.resolve(target.getOrElse(""))
        .resolve("release")
        .resolve("libcpu.a")
    inputs.file(lib)

    // Windows の "bash" は WSL の入口 (System32\bash.exe) に解決されることがあるため、Git 同梱の sh を使う
    val shell = if (System.getProperty("os.name").startsWith("Windows")) "sh" else "bash"
    commandLine(shell, rootProject.file("script/rename-static-symbols.sh").path, lib.path, "com_wsr_cpu_")
}

cargoNativeBuild { finalizedBy(cargoNativeRename) }

tasks.register<Delete>("clean") {
    group = "build"
    description = "ビルド成果物の削除"
    fileTree(targetDir).also { delete(it) }
}
