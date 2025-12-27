package com.wsr.batch.collecction.sum

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

fun Batch<IOType.D2>.sum(): Batch<IOType.D0> {
    val result = Backend.sum(x = value, xb = size)
    return Batch(size = size, shape = listOf(1), value = result)
}

fun Batch<IOType.D2>.sum(axis: Int): Batch<IOType.D1> {
    val result = Backend.sum(x = value, xi = size, xj = shape[0], xk = shape[1], axis = axis + 1)
    return Batch(size = size, shape = listOf(if (axis == 0) shape[1] else shape[0]), value = result)
}
