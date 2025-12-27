package com.wsr

import com.wsr.base.DataBuffer
import com.wsr.base.IBackend
import com.wsr.base.KotlinBackend

object Backend : IBackend by KotlinBackend {
    override fun inner(x: DataBuffer, y: DataBuffer, b: Int): DataBuffer = BLAS.sgemm(
        m = 1,
        n = 1,
        k = x.size,
        alpha = 1f,
        a = x,
        transA = false,
        b = y,
        transB = true,
        beta = 0f,
        c = DataBuffer.create(b),
        batchSize = b,
    )

    override fun matMul(x: DataBuffer, transX: Boolean, y: DataBuffer, m: Int, k: Int): DataBuffer = BLAS.sgemm(
        m = m,
        n = 1,
        k = k,
        alpha = 1f,
        a = x,
        transA = transX,
        b = y,
        transB = false,
        beta = 0f,
        c = DataBuffer.create(m),
        batchSize = 1,
    )

    override fun matMul(x: DataBuffer, y: DataBuffer, transY: Boolean, n: Int, k: Int): DataBuffer = BLAS.sgemm(
        m = 1,
        n = n,
        k = k,
        alpha = 1f,
        a = x,
        transA = false,
        b = y,
        transB = transY,
        beta = 0f,
        c = DataBuffer.create(n),
        batchSize = 1,
    )

    override fun matMul(
        x: DataBuffer,
        transX: Boolean,
        y: DataBuffer,
        transY: Boolean,
        m: Int,
        n: Int,
        k: Int,
        b: Int,
    ): DataBuffer = BLAS.sgemm(
        m = m,
        n = n,
        k = k,
        alpha = 1f,
        a = x,
        transA = transX,
        b = y,
        transB = transY,
        beta = 0f,
        c = DataBuffer.create(b * m * n),
        batchSize = b,
    )
}
