package com.wsr.core.reshape.broadcast

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

fun IOType.D1.broadcastToD2(axis: Int, size: Int) = when (axis) {
    0 -> IOType.d2(size, shape[0]) { x, y -> this[y] }
    1 -> IOType.d2(shape[0], size) { x, y -> this[x] }
    else -> throw IllegalArgumentException("IOType.D1.broadcastToD2 axis is $axis not 0 or 1.")
}

fun IOType.D2.broadcastToD3(axis: Int, size: Int) = when (axis) {
    0 -> IOType.d3(size, shape[0], shape[1]) { i, j, k -> this[j, k] }
    1 -> IOType.d3(shape[0], size, shape[1]) { i, j, k -> this[i, k] }
    2 -> IOType.d3(shape[0], shape[1], size) { i, j, k -> this[i, j] }
    else -> throw IllegalArgumentException("IOType.D2.broadcastToD3 axis is $axis not 0, 1 or 2.")
}
