package com.wsr.open

import com.wsr.blas.base.IBLAS
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val LIB_NAME = "open_blas"
private val LIB_PATH = createPath("open")

actual fun loadOpenBLAS(): IBLAS? {
    val resource = System.mapLibraryName(LIB_NAME)
    val result = IBLAS::class.java
        .classLoader
        .getResourceAsStream(LIB_PATH + resource)
        ?.use { inputStream ->
            val path = Files
                .createTempDirectory(LIB_NAME)
                .also { it.toFile().deleteOnExit() }
                .resolve(resource)
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
            System.load(path.toString())
        }
    return if (result != null) JOpenBLAS() else null
}
