package com.wsr.core.reshape

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

fun List<IOType.D2>.toD3(): IOType.D3 = IOType.d3(
    i = size,
    j = first().shape[0],
    k = first().shape[1],
) { i, j, k -> this[i][j, k] }

fun IOType.D2.slice(i: IntRange = 0 until shape[0], j: IntRange = 0 until shape[1]) =
    IOType.d2(shape = listOf(i.count(), j.count())) { x, y ->
        this[i.start + x, j.start + y]
    }

fun IOType.D2.transpose() = IOType.d2(shape.reversed()) { x, y -> this[y, x] }

fun List<IOType.D2>.transpose() = this.map { it.transpose() }

fun IOType.D2.reshapeToD3(i: Int, j: Int, k: Int) = reshapeToD3(shape = listOf(i, j, k))

fun IOType.D2.reshapeToD3(shape: List<Int>) = IOType.d3(shape = shape, value = value)

fun IOType.D2.broadcastToD3(axis: Int, size: Int) = when (axis) {
    0 -> IOType.d3(size, shape[0], shape[1]) { i, j, k -> this[j, k] }
    1 -> IOType.d3(shape[0], size, shape[1]) { i, j, k -> this[i, k] }
    2 -> IOType.d3(shape[0], shape[1], size) { i, j, k -> this[i, j] }
    else -> throw IllegalArgumentException("IOType.D2.broadcastToD3 axis is $axis not 0, 1 or 2.")
}
