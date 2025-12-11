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

    override fun sdot(n: Int, x: DataBuffer, incX: Int, y: DataBuffer, incY: Int): Float =
        instance.sdot(n, x, incX, y, incY)

    override fun sscal(n: Int, alpha: Float, x: DataBuffer, incX: Int) {
        instance.sscal(n, alpha, x, incX)
    }

    override fun saxpy(n: Int, alpha: Float, x: DataBuffer, incX: Int, y: DataBuffer, incY: Int) {
        instance.saxpy(n, alpha, x, incX, y, incY)
    }

    override fun sgemv(
        trans: Boolean,
        m: Int,
        n: Int,
        alpha: Float,
        a: DataBuffer,
        lda: Int,
        x: DataBuffer,
        incX: Int,
        beta: Float,
        y: DataBuffer,
        incY: Int,
    ) {
        instance.sgemv(trans, m, n, alpha, a, lda, x, incX, beta, y, incY)
    }

    override fun sgemm(
        transA: Boolean,
        transB: Boolean,
        m: Int,
        n: Int,
        k: Int,
        alpha: Float,
        a: DataBuffer,
        lda: Int,
        b: DataBuffer,
        ldb: Int,
        beta: Float,
        c: DataBuffer,
        ldc: Int,
    ) {
        instance.sgemm(transA, transB, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc)
    }

    override fun sdot2(x: DataBuffer, y: DataBuffer): Float {
        return instance.sdot2(x, y)
    }

    override fun sscal2(alpha: Float, x: DataBuffer): DataBuffer {
        return instance.sscal2(alpha, x)
    }

    override fun saxpy2(alpha: Float, x: DataBuffer, y: DataBuffer): DataBuffer {
        return instance.saxpy2(alpha, x, y)
    }

    override fun sgemv2(
        row: Int,
        col: Int,
        alpha: Float,
        a: DataBuffer,
        x: DataBuffer,
        beta: Float,
        y: DataBuffer
    ): DataBuffer {
        return instance.sgemv2(row, col, alpha, a, x, beta, y)
    }

    override fun sgemm2(
        m: Int,
        n: Int,
        k: Int,
        alpha: Float,
        a: DataBuffer,
        b: DataBuffer,
        beta: Float,
        c: DataBuffer
    ): DataBuffer {
        return instance.sgemm2(m, n, k, alpha, a, b, beta, c)
    }
}
