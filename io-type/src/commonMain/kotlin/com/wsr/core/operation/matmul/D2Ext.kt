package com.wsr.core.operation.matmul

import com.wsr.BLAS
import com.wsr.base.DataBuffer
import com.wsr.core.IOType

fun IOType.D2.matMul(other: IOType.D1, trans: Boolean = false): IOType.D1 {
    val result = BLAS.sgemv(
        row = shape[0],
        col = shape[1],
        alpha = 1f,
        a = value,
        trans = trans,
        x = other.value,
        beta = 0f,
        y = DataBuffer.create(if(trans) shape[1] else shape[0]),
    )
    return IOType.D1(result)
}

fun IOType.D2.matMul(other: IOType.D2, transA: Boolean = false, transB: Boolean = false): IOType.D2 {
    val m = if (transA) shape[1] else shape[0]
    val n = if (transB) other.shape[0] else other.shape[1]
    val k = if (transA) shape[0] else shape[1]
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
        c = DataBuffer.create(m * n),
        batchSize = 1,
    )
    return IOType.D2(result, listOf(m, n))
}
