package com.wsr.batch.operation.inner

import com.wsr.BLAS
import com.wsr.base.DataBuffer
import com.wsr.batch.Batch
import com.wsr.core.IOType

@JvmName("batchD1sInnerToD1s")
infix fun Batch<IOType.D1>.inner(other: Batch<IOType.D1>): Batch<IOType.D0> {
    val result = BLAS.sgemm(
        m = 1,
        n = 1,
        k = shape[0],
        alpha = 1f,
        a = value,
        b = other.value,
        beta = 0f,
        c = DataBuffer.create(size * 1 * 1),
        batchSize = size,
    )
    return Batch(value = result, size = size, shape = listOf(1))
}
