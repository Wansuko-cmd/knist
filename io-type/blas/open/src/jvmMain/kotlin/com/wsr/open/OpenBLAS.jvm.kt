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

    override fun sdot(x: DataBuffer, y: DataBuffer): Float = instance.sdot(
        x.size,
        x.toFloatArray(),
        1,
        y.toFloatArray(),
        1,
    )

    override fun sscal(alpha: Float, x: DataBuffer): DataBuffer {
        val result = x.toFloatArray()
        instance.sscal(
            x.size,
            alpha,
            result,
            1,
        )
        return DataBuffer.create(result)
    }

    override fun saxpy(alpha: Float, x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = y.toFloatArray()
        instance.saxpy(
            x.size,
            alpha,
            x.toFloatArray(),
            1,
            result,
            1,
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
            false,
            row,
            col,
            alpha,
            a.toFloatArray(),
            col,
            x.toFloatArray(),
            1,
            beta,
            result,
            1,
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
        batchSize: Int,
    ): DataBuffer {
        val result = c.toFloatArray()
        instance.sgemm(
            false,
            false,
            m,
            n,
            k,
            alpha,
            a.toFloatArray(),
            k,
            b.toFloatArray(),
            n,
            beta,
            result,
            n,
            batchSize,
        )
        return DataBuffer.create(result)
    }
}
