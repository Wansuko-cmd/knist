package com.wsr.batch.collecction.average

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.average.average
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus

@JvmName("batchD1sAverageBatch")
fun Batch<IOType.D1>.average(): Batch<IOType.D0> {
    val sum = Backend.sum(x = value, xi = size, xj = step, axis = 1)
    val result = Backend.div(x = sum, y = step.toFloat())
    return Batch(size = size, shape = listOf(1), value = result)
}

@JvmName("batchD1sBatchAverage")
fun Batch<IOType.D1>.batchAverage(): IOType.D1 {
    val sum = Backend.sum(x = value, xi = size, xj = step, axis = 0)
    return IOType.D1(value = Backend.div(x = sum, y = size.toFloat()))
}
