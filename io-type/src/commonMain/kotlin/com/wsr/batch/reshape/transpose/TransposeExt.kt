package com.wsr.batch.reshape.transpose

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

fun Batch<IOType.D2>.transpose(): Batch<IOType.D2> {
    val result = Backend.transpose(x = value, xi = size, xj = shape[0], xk = shape[1], axisI = 0, axisJ = 2, axisK = 1)
    return Batch(size = size, shape = shape.reversed(), value = result)
}

fun Batch<IOType.D3>.transpose(axisI: Int, axisJ: Int, axisK: Int): Batch<IOType.D3> {
    val result = Backend.transpose(
        x = value,
        xi = size,
        xj = shape[0],
        xk = shape[1],
        xl = shape[2],
        axisI = 0,
        axisJ = axisI + 1,
        axisK = axisJ + 1,
        axisL = axisK + 1,
    )
    return Batch(size = size, shape = listOf(shape[axisI], shape[axisJ], shape[axisK]), value = result)
}
