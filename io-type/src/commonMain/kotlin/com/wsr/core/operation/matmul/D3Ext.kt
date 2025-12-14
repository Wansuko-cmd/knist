package com.wsr.core.operation.matmul

import com.wsr.BLAS
import com.wsr.base.DataBuffer
import com.wsr.core.IOType

fun IOType.D3.matMul(other: IOType.D3, transA: Boolean = false, transB: Boolean = false): IOType.D3 {
    val m = if (transA) shape[2] else shape[1]
    val n = if (transB) other.shape[1] else other.shape[2]
    val k = if (transA) shape[1] else shape[2]
    val result = BLAS.sgemm(
        m = m,
        n = n,
        k = k,
        alpha = 1f,
        a = value,
        transA = transA,
        b = other.value,
        transB = transB,
        beta = 0f,
        c = DataBuffer.create(shape[0] * m * n),
        batchSize = shape[0],
    )
    return IOType.D3(shape = listOf(shape[0], m, n), value = result)
}
