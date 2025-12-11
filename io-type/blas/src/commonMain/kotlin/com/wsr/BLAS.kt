package com.wsr

import com.wsr.blas.base.DataBuffer
import com.wsr.blas.base.IBLAS
import com.wsr.open.openBLAS

object BLAS : IBLAS {
    private var instance: IBLAS = openBLAS

    fun set(blas: IBLAS) {
        instance = blas
    }

    override val isNative: Boolean = instance.isNative

    override fun sdot(x: DataBuffer, y: DataBuffer): Float {
        return instance.sdot(x, y)
    }

    override fun sscal(alpha: Float, x: DataBuffer): DataBuffer {
        return instance.sscal(alpha, x)
    }

    override fun saxpy(alpha: Float, x: DataBuffer, y: DataBuffer): DataBuffer {
        return instance.saxpy(alpha, x, y)
    }

    override fun sgemv(
        row: Int,
        col: Int,
        alpha: Float,
        a: DataBuffer,
        x: DataBuffer,
        beta: Float,
        y: DataBuffer
    ): DataBuffer {
        return instance.sgemv(row, col, alpha, a, x, beta, y)
    }

    override fun sgemm(
        m: Int,
        n: Int,
        k: Int,
        alpha: Float,
        a: DataBuffer,
        b: DataBuffer,
        beta: Float,
        c: DataBuffer
    ): DataBuffer {
        return instance.sgemm(m, n, k, alpha, a, b, beta, c)
    }
}
