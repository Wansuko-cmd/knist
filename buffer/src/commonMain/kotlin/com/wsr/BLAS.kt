package com.wsr

import com.wsr.base.DataBuffer
import com.wsr.base.IBLAS
import com.wsr.open.openBLAS

object BLAS : IBLAS {
    private var instance: IBLAS = openBLAS

    fun set(blas: IBLAS) {
        instance = blas
    }

    override val isNative: Boolean = instance.isNative

    override fun sdot(x: DataBuffer, y: DataBuffer): Float = instance.sdot(x, y)

    override fun sscal(alpha: Float, x: DataBuffer): DataBuffer = instance.sscal(alpha, x)

    override fun saxpy(alpha: Float, x: DataBuffer, y: DataBuffer): DataBuffer = instance.saxpy(alpha, x, y)

    override fun sgemv(
        row: Int,
        col: Int,
        alpha: Float,
        a: DataBuffer,
        trans: Boolean,
        x: DataBuffer,
        beta: Float,
        y: DataBuffer,
    ): DataBuffer = instance.sgemv(row, col, alpha, a, trans, x, beta, y)

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
    ): DataBuffer = instance.sgemm(m, n, k, alpha, a, transA, b, transB, beta, c, batchSize)
}
