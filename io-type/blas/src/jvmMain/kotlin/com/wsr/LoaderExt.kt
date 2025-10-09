package com.wsr

import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val name = "JBLAS"

internal fun loadJBLAS(): JBLAS? {
    val resource = System.mapLibraryName(name)
    val result = BLAS::class.java
        .classLoader
        .getResourceAsStream(resource)
        ?.use { inputStream ->
            val path = Files
                .createTempDirectory(name)
                .also { it.toFile().deleteOnExit() }
                .resolve(resource)
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
            System.load(path.toString())
        }
    return if (result != null) JBLAS() else null
}
