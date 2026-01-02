package com.wsr.batch.collecction.average

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

@JvmName("batchD3sBatchAverage")
fun Batch<IOType.D4>.batchAverage(): IOType.D4 {
    val result = Backend.average(x = value, xi = size, xj = step, axis = 0)
    return IOType.D4(shape = shape, value = result)
}
