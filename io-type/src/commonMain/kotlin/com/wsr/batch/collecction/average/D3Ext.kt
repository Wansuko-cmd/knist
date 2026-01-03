package com.wsr.batch.collecction.average

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus

@JvmName("batchD3sAverageBatch")
fun Batch<IOType.D3>.average(): Batch<IOType.D0> {
    val result = Backend.average(x = value, xi = size, xj = step, axis = 1)
    return Batch(size = size, shape = listOf(1), value = result)
}

@JvmName("batchD2sAverageWithAxis")
fun Batch<IOType.D3>.average(axis: Int): Batch<IOType.D2> {
    val result = Backend.average(x = value, xi = size, xj = shape[0], xk = shape[1], xl = shape[2], axis = axis + 1)
    return Batch(
        size = size,
        shape = when (axis) {
            0 -> listOf(shape[1], shape[2])
            1 -> listOf(shape[0], shape[2])
            else -> listOf(shape[0], shape[1])
        },
        value = result,
    )
}

@JvmName("batchD3sBatchAverage")
fun Batch<IOType.D3>.batchAverage(): IOType.D3 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
