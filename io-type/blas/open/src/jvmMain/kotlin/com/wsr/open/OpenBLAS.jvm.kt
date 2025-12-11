package com.wsr.open

import com.wsr.blas.base.DataBuffer
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
    return if (result != null) OpenBLAS() else null
}

class OpenBLAS internal constructor() : IBLAS {
    private val instance: JOpenBLAS = JOpenBLAS()

    override val isNative: Boolean = true

    override fun sdot(x: DataBuffer, y: DataBuffer): Float {
        return instance.sdot(
            /* n = */ x.size,
            /* x = */ x.toFloatArray(),
            /* incX = */ 1,
            /* y = */ y.toFloatArray(),
            /* incY = */ 1,
        )
    }

    override fun sscal(alpha: Float, x: DataBuffer): DataBuffer {
        val result = x.toFloatArray()
        instance.sscal(
            /* n = */ x.size,
            /* alpha = */ alpha,
            /* x = */ result,
            /* incX = */ 1,
        )
        return DataBuffer.create(result)
    }

    override fun saxpy(alpha: Float, x: DataBuffer, y: DataBuffer,): DataBuffer {
        val result = y.toFloatArray()
        instance.saxpy(
            /* n = */ x.size,
            /* alpha = */ alpha,
            /* x = */ x.toFloatArray(),
            /* incX = */ 1,
            /* y = */ result,
            /* incY = */ 1,
        )
        return DataBuffer.create(result)
    }

    override fun sgemv(
        row: Int,
        col: Int,
        alpha: Float,
        a: DataBuffer,
        x: DataBuffer,
        beta: Float,
        y: DataBuffer,
    ): DataBuffer {
        val result = y.toFloatArray()
        instance.sgemv(
            /* trans = */ false,
            /* m = */ row,
            /* n = */ col,
            /* alpha = */ alpha,
            /* a = */ a.toFloatArray(),
            /* lda = */ col,
            /* x = */ x.toFloatArray(),
            /* incX = */ 1,
            /* beta = */ beta,
            /* y = */ result,
            /* incY  = */ 1,
        )
        return DataBuffer.create(result)
    }

    override fun sgemm(
        m: Int,
        n: Int,
        k: Int,
        alpha: Float,
        a: DataBuffer,
        b: DataBuffer,
        beta: Float,
        c: DataBuffer,
    ): DataBuffer {
        val result = c.toFloatArray()
        instance.sgemm(
            /* transA = */ false,
            /* transB = */ false,
            /* m = */ m,
            /* n = */ n,
            /* k = */ k,
            /* alpha = */ alpha,
            /* a = */ a.toFloatArray(),
            /* lda = */ k,
            /* b = */ b.toFloatArray(),
            /* ldb = */ n,
            /* beta = */ beta,
            /* c = */ result,
            /* ldc = */ n,
        )
        return DataBuffer.create(result)
    }
}
