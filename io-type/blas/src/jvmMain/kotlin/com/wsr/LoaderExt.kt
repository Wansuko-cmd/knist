package com.wsr

import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val LIB_NAME = "JBLAS"

internal fun loadJBLAS(): JBLAS? {
    val resource = System.mapLibraryName(LIB_NAME)
    val result = BLAS::class.java
        .classLoader
        .getResourceAsStream(resource)
        ?.use { inputStream ->
            val path = Files
                .createTempDirectory(LIB_NAME)
                .also { it.toFile().deleteOnExit() }
                .resolve(resource)
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
            System.load(path.toString())
        }
    return if (result != null) JBLAS() else null
}
