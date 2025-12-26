package com.wsr.batch.collecction.sum

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

fun Batch<IOType.D3>.sum(): Batch<IOType.D0> {
    val result = Backend.sum(x = value, xb = size)
    return Batch(size = size, shape = listOf(1), value = result)
}

fun Batch<IOType.D3>.sum(axis: Int): Batch<IOType.D2> {
    val result = Backend.sum(x = value, xi = size, xj = shape[0], xk = shape[1], xl = shape[2], axis = axis + 1)
    return Batch(
        size = size,
        shape = when (axis) {
            0 -> listOf(shape[1], shape[2])
            1 -> listOf(shape[0], shape[2])
            else -> listOf(shape[0], shape[1])
        },
        value = result
    )
}
