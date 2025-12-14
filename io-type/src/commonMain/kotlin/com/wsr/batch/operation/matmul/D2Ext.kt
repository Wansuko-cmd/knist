package com.wsr.batch.operation.matmul

import com.wsr.BLAS
import com.wsr.base.DataBuffer
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.operation.matmul.matMul

infix fun IOType.D2.matMul(other: Batch<IOType.D1>) = other.map { this matMul it }

@JvmName("matMulToD2s")
infix fun Batch<IOType.D2>.matMul(other: IOType.D2): Batch<IOType.D2> {
    val result = BLAS.sgemm(
        m = size * shape[0],
        n = other.shape[1],
        k = shape[1],
        alpha = 1f,
        a = value,
        b = other.value,
        beta = 0f,
        c = DataBuffer.create(size * shape[0] * other.shape[1]),
        batchSize = 1,
    )
    return Batch(value = result, size = size, shape = listOf(shape[0], other.shape[1]))
}

@JvmName("matMulToD2s")
infix fun Batch<IOType.D2>.matMul(other: Batch<IOType.D2>): Batch<IOType.D2> {
    val result = BLAS.sgemm(
        m = shape[0],
        n = other.shape[1],
        k = shape[1],
        alpha = 1f,
        a = value,
        b = other.value,
        beta = 0f,
        c = DataBuffer.create(size * shape[0] * other.shape[1]),
        batchSize = size,
    )
    return Batch(value = result, size = size, shape = listOf(shape[0], other.shape[1]))
}
