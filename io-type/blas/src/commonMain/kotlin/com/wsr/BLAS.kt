package com.wsr

import com.wsr.blas.base.IBLAS
import com.wsr.open.openBLAS

object BLAS : IBLAS {
    private var instance: IBLAS = openBLAS

    fun set(blas: IBLAS) {
        instance = blas
    }

    override val isNative: Boolean = instance.isNative

    override fun sdot(n: Int, x: FloatArray, incX: Int, y: FloatArray, incY: Int): Float =
        instance.sdot(n, x, incX, y, incY)

    override fun sscal(n: Int, alpha: Float, x: FloatArray, incX: Int) {
        instance.sscal(n, alpha, x, incX)
    }

    override fun saxpy(n: Int, alpha: Float, x: FloatArray, incX: Int, y: FloatArray, incY: Int) {
        instance.saxpy(n, alpha, x, incX, y, incY)
    }

    override fun sgemv(
        trans: Boolean,
        m: Int,
        n: Int,
        alpha: Float,
        a: FloatArray,
        lda: Int,
        x: FloatArray,
        incX: Int,
        beta: Float,
        y: FloatArray,
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
        a: FloatArray,
        lda: Int,
        b: FloatArray,
        ldb: Int,
        beta: Float,
        c: FloatArray,
        ldc: Int,
    ) {
        instance.sgemm(transA, transB, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc)
    }

    fun sdot(n: Int, x: DataBuffer, incX: Int, y: DataBuffer, incY: Int): Float =
        instance.sdot(n, x.toFloatArray(), incX, y.toFloatArray(), incY)

    fun sscal(n: Int, alpha: Float, x: DataBuffer, incX: Int) {
        instance.sscal(n, alpha, x.toFloatArray(), incX)
    }

    fun saxpy(n: Int, alpha: Float, x: DataBuffer, incX: Int, y: DataBuffer, incY: Int) {
        instance.saxpy(n, alpha, x.toFloatArray(), incX, y.toFloatArray(), incY)
    }

    fun sgemv(
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
        instance.sgemv(trans, m, n, alpha, a.toFloatArray(), lda, x.toFloatArray(), incX, beta, y.toFloatArray(), incY)
    }

    fun sgemm(
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
        instance.sgemm(transA, transB, m, n, k, alpha, a.toFloatArray(), lda, b.toFloatArray(), ldb, beta, c.toFloatArray(), ldc)
    }
}
