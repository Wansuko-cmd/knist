package com.wsr

import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val LIB_NAME = "open_blas"
private val LIB_PATH = createPath("open")

val openBLAS: IBLAS? by lazy {
    val resource = System.mapLibraryName( LIB_NAME)
    val result = BLAS::class.java
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
    if (result != null) JOpenBLAS() else null
}
