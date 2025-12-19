package com.wsr.core.operation.times

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.zip.zipWith

operator fun IOType.D2.times(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other }

fun IOType.D2.times2(other: IOType.D1, axis: Int): IOType.D2 = zipWith(other, axis) { a, b -> a * b }

operator fun IOType.D2.times(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other[i, j] }

operator fun IOType.D2.times(other: IOType.D3): IOType.D3 = this.times(other = other, axis = 0)

fun IOType.D2.times(other: IOType.D3, axis: Int): IOType.D3 = when (axis) {
    0 -> IOType.d3(shape) { i, j, k -> this[j, k] * other[i, j, k] }
    1 -> IOType.d3(shape) { i, j, k -> this[i, k] * other[i, j, k] }
    2 -> IOType.d3(shape) { i, j, k -> this[i, j] * other[i, j, k] }
    else -> throw IllegalArgumentException("IOType.D2.times axis is $axis not 0, 1 or 2.")
}
