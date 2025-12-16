package com.wsr.core.operation.minus

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

operator fun IOType.D2.minus(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other }

operator fun IOType.D2.minus(other: IOType.D0): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other.get() }

operator fun IOType.D2.minus(other: IOType.D1): IOType.D2 = this.minus(other = other, axis = 0)

fun IOType.D2.minus(other: IOType.D1, axis: Int): IOType.D2 = when (axis) {
    0 -> IOType.d2(shape) { i, j -> this[i, j] - other[j] }
    1 -> IOType.d2(shape) { i, j -> this[i, j] - other[i] }
    else -> throw IllegalArgumentException("IOType.D2.minus axis is $axis not 0 or 1.")
}

operator fun IOType.D2.minus(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other[i, j] }

operator fun IOType.D2.minus(other: IOType.D3): IOType.D3 = this.minus(other = other, axis = 0)

fun IOType.D2.minus(other: IOType.D3, axis: Int): IOType.D3 = when (axis) {
    0 -> IOType.d3(shape) { i, j, k -> this[j, k] - other[i, j, k] }
    1 -> IOType.d3(shape) { i, j, k -> this[i, k] - other[i, j, k] }
    2 -> IOType.d3(shape) { i, j, k -> this[i, j] - other[i, j, k] }
    else -> throw IllegalArgumentException("IOType.D2.minus axis is $axis not 0, 1 or 2.")
}
