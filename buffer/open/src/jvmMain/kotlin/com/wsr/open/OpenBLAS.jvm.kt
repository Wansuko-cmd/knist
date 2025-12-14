package com.wsr.open

import com.wsr.base.DataBuffer
import com.wsr.base.IBLAS
import com.wsr.base.loadNativeLibrary

private const val LIB_PATH = "open"
private const val LIB_NAME = "open_blas"

actual fun loadOpenBLAS(): IBLAS? {
    val isSuccess = loadNativeLibrary(path = LIB_PATH, name = LIB_NAME)
    return if (isSuccess) OpenBLAS() else null
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
        trans: Boolean,
        x: DataBuffer,
        beta: Float,
        y: DataBuffer,
    ): DataBuffer {
        val result = y.toFloatArray()
        instance.sgemv(
            trans,
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
        transA: Boolean,
        b: DataBuffer,
        transB: Boolean,
        beta: Float,
        c: DataBuffer,
        batchSize: Int,
    ): DataBuffer {
        val result = c.toFloatArray()
        instance.sgemm(
            transA,
            transB,
            m,
            n,
            k,
            alpha,
            a.toFloatArray(),
            if (transA) m else k,
            b.toFloatArray(),
            if (transB) k else n,
            beta,
            result,
            n,
            batchSize,
        )
        return DataBuffer.create(result)
    }
}
