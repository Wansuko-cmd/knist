package com.wsr.core.operation.plus

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

operator fun IOType.D2.plus(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] + other }

operator fun IOType.D2.plus(other: IOType.D0): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] + other.get() }

fun IOType.D2.plus2(other: IOType.D1, axis: Int): IOType.D2 = when (axis) {
    0 -> IOType.d2(shape) { i, j -> this[i, j] + other[i] }
    1 -> IOType.d2(shape) { i, j -> this[i, j] + other[j] }
    else -> throw IllegalArgumentException("IOType.D2.plus axis is $axis not 0 or 1.")
}

operator fun IOType.D2.plus(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] + other[i, j] }

operator fun IOType.D2.plus(other: IOType.D3): IOType.D3 = this.plus(other = other, axis = 0)

fun IOType.D2.plus(other: IOType.D3, axis: Int): IOType.D3 = when (axis) {
    0 -> IOType.d3(shape) { i, j, k -> this[j, k] + other[i, j, k] }
    1 -> IOType.d3(shape) { i, j, k -> this[i, k] + other[i, j, k] }
    2 -> IOType.d3(shape) { i, j, k -> this[i, j] + other[i, j, k] }
    else -> throw IllegalArgumentException("IOType.D2.plus axis is $axis not 0, 1 or 2.")
}
