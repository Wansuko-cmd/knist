package com.wsr.reshape

import com.wsr.IOType

fun List<IOType.D1>.toD2(): IOType.D2 = IOType.d2(size, first().shape[0]) { i, j -> this[i][j] }

fun IOType.D1.broadcastToD2(axis: Int, size: Int) = when (axis) {
    0 -> IOType.d2(shape[0], size) { x, y -> this[x] }
    1 -> IOType.d2(size, shape[0]) { x, y -> this[y] }
    else -> throw IllegalArgumentException("IOType.D1.broadcastToD2 axis is $axis not 0 or 1.")
}
