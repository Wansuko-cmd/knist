package com.wsr.base

import java.nio.file.Files
import java.nio.file.StandardCopyOption

fun loadNativeLibrary(path: String, name: String): Boolean {
    val resource = System.mapLibraryName(name)
    val result = IBLAS::class.java
        .classLoader
        .getResourceAsStream(createLibPath(path) + resource)
        ?.use { inputStream ->
            val path = Files
                .createTempDirectory(name)
                .also { it.toFile().deleteOnExit() }
                .resolve(resource)
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
            System.load(path.toString())
        }
    return result != null
}
private fun createLibPath(path: String) = "$path/$os/$arch/"

private val os: String
    get() {
        val osProperty: String = System.getProperty("os.name").lowercase()
        return when {
            osProperty.contains("mac") -> "macos"
            System.getProperty("java.vm.name").contains("Dalvik") -> "android"
            osProperty.contains("nux") -> "linux"
            osProperty.contains("win") -> "mingw"
            else -> error("Unsupported operating system: $osProperty")
        }
    }

private val arch: String
    get() = when (val arch: String = System.getProperty("os.arch").lowercase()) {
        "amd64", "x86_64", "x86-64", "x64" -> "X64"
        "arm64", "aarch64", "armv8" -> "Arm64"
        else -> error("Unsupported architecture: $arch")
    }
