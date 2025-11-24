package com.wsr.core.reshape

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get

fun List<IOType.D1>.toD2(): IOType.D2 = IOType.d2(size, first().shape[0]) { i, j -> this[i][j] }

fun IOType.D1.slice(i: IntRange) = IOType.d1(i.count()) { this[i.start + it] }

fun IOType.D1.broadcastToD2(axis: Int, size: Int) = when (axis) {
    0 -> IOType.d2(size, shape[0]) { x, y -> this[y] }
    1 -> IOType.d2(shape[0], size) { x, y -> this[x] }
    else -> throw IllegalArgumentException("IOType.D1.broadcastToD2 axis is $axis not 0 or 1.")
}
