package com.wsr.batch.operation.matmul

import com.wsr.BLAS
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.blas.base.DataBuffer
import com.wsr.core.IOType
import com.wsr.core.operation.matmul.matMul

infix fun IOType.D3.matMul(other: Batch<IOType.D3>) = other.map { this matMul it }

@JvmName("matMulToD3s")
infix fun Batch<IOType.D3>.matMul(other: IOType.D3): Batch<IOType.D3> = map { it matMul other }

@JvmName("matMulToD3s")
infix fun Batch<IOType.D3>.matMul(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = BLAS.sgemm(
        m = shape[1],
        n = other.shape[2],
        k = shape[2],
        alpha = 1f,
        a = value,
        b = other.value,
        beta = 0f,
        c = DataBuffer.create(size * shape[0] * shape[1] * other.shape[2]),
        batchSize = size * shape[0],
    )
    return Batch(value = result, size = size, shape = listOf(shape[0], shape[1], other.shape[2]))
}
