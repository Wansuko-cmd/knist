package com.wsr.batch.operation.inner

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

@JvmName("batchD1sInnerToD1s")
infix fun Batch<IOType.D1>.inner(other: Batch<IOType.D1>): Batch<IOType.D0> {
    val result = Backend.inner(x = value, y = other.value, b = size)
    return Batch(value = result, size = size, shape = listOf(1))
}
