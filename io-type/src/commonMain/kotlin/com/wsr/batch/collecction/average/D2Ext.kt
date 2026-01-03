package com.wsr.batch.collecction.average

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus

@JvmName("batchD2sAverageBatch")
fun Batch<IOType.D2>.average(): Batch<IOType.D0> {
    val result = Backend.average(x = value, xi = size, xj = step, axis = 1)
    return Batch(size = size, shape = listOf(1), value = result)
}

@JvmName("batchD2sAverageWithAxis")
fun Batch<IOType.D2>.average(axis: Int): Batch<IOType.D1> {
    val result = Backend.average(x = value, xi = size, xj = shape[0], xk = shape[1], axis = axis + 1)
    return Batch(
        size = size,
        shape = when (axis) {
            0 -> listOf(shape[1])
            else -> listOf(shape[0])
        },
        value = result,
    )
}

@JvmName("batchD2sBatchAverage")
fun Batch<IOType.D2>.batchAverage(): IOType.D2 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
