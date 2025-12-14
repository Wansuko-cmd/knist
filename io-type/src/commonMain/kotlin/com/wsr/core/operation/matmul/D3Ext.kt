package com.wsr.core.operation.matmul

import com.wsr.BLAS
import com.wsr.base.DataBuffer
import com.wsr.core.IOType

infix fun IOType.D3.matMul(other: IOType.D3): IOType.D3 {
    val result = BLAS.sgemm(
        m = shape[1],
        n = other.shape[2],
        k = shape[2],
        alpha = 1f,
        a = value,
        b = other.value,
        beta = 0f,
        c = DataBuffer.create(shape[0] * shape[1] * other.shape[2]),
        batchSize = shape[0],
    )
    return IOType.D3(shape = listOf(shape[0], shape[1], other.shape[2]), value = result)
}
