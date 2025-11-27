package com.wsr

internal fun createPath(name: String) = "$name/$os/$arch/"

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
