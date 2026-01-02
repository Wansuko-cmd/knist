package com.wsr.batch.collecction.average

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.average.average
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus

@JvmName("batchD2sAverageBatch")
fun Batch<IOType.D2>.average(): Batch<IOType.D0> {
    val sum = Backend.sum(x = value, xi = size, xj = step, axis = 1)
    val result = Backend.div(x = sum, y = step.toFloat())
    return Batch(size = size, shape = listOf(1), value = result)
}

@JvmName("batchD2sAverageWithAxis")
fun Batch<IOType.D2>.average(axis: Int): Batch<IOType.D1> {
    val sum = Backend.sum(x = value, xi = size, xj = shape[0], xk = shape[1], axis = axis + 1)
    val result = Backend.div(x = sum, y = shape[axis].toFloat())
    return Batch(
        size = size,
        shape = listOf(result.size),
        value = result,
    )
}

@JvmName("batchD2sBatchAverage")
fun Batch<IOType.D2>.batchAverage(): IOType.D2 {
    val sum = Backend.sum(x = value, xi = size, xj = step, axis = 0)
    return IOType.D2(shape = shape, value = Backend.div(x = sum, y = size.toFloat()))
}
