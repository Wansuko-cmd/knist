package com.wsr.batch.operation.matmul

import com.wsr.BLAS
import com.wsr.base.DataBuffer
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.operation.matmul.matMul

fun IOType.D2.matMul(other: Batch<IOType.D1>, trans: Boolean = false) = other.map { this.matMul(it, trans) }

@JvmName("matMulToD2s")
fun Batch<IOType.D2>.matMul(other: IOType.D2, transA: Boolean = false, transB: Boolean = false): Batch<IOType.D2> {
    val m = size * if (transA) shape[1] else shape[0]
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
    return Batch(value = result, size = size, shape = listOf(m, n))
}

@JvmName("matMulToD2s")
fun Batch<IOType.D2>.matMul(other: Batch<IOType.D2>, transA: Boolean = false, transB: Boolean = false): Batch<IOType.D2> {
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
        c = DataBuffer.create(size * m * n),
        batchSize = size,
    )
    return Batch(value = result, size = size, shape = listOf(m, n))
}
