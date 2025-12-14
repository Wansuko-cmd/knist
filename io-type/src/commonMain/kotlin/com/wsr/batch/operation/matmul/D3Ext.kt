package com.wsr.batch.operation.matmul

import com.wsr.BLAS
import com.wsr.base.DataBuffer
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.operation.matmul.matMul

fun IOType.D3.matMul(other: Batch<IOType.D3>, transA: Boolean = false, transB: Boolean = false) = other.map { this.matMul(it, transA, transB) }

@JvmName("matMulToD3s")
fun Batch<IOType.D3>.matMul(other: IOType.D3, transA: Boolean = false, transB: Boolean = false): Batch<IOType.D3> = map { it.matMul(other, transA, transB) }

@JvmName("matMulToD3s")
fun Batch<IOType.D3>.matMul(other: Batch<IOType.D3>, transA: Boolean = false, transB: Boolean = false): Batch<IOType.D3> {
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
        c = DataBuffer.create(size * shape[0] * m * n),
        batchSize = size * shape[0],
    )
    return Batch(value = result, size = size, shape = listOf(shape[0], m, n))
}
