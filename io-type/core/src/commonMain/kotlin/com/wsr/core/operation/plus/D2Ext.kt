package com.wsr.core.operation.plus

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get

operator fun IOType.D2.plus(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] + other }

operator fun IOType.D2.plus(other: IOType.D0): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] + other.get() }

operator fun IOType.D2.plus(other: IOType.D1): IOType.D2 = this.plus(other = other, axis = 0)

fun IOType.D2.plus(other: IOType.D1, axis: Int): IOType.D2 = when (axis) {
    0 -> IOType.d2(shape) { i, j -> this[i, j] + other[j] }
    1 -> IOType.d2(shape) { i, j -> this[i, j] + other[i] }
    else -> throw IllegalArgumentException("IOType.D2.plus axis is $axis not 0 or 1.")
}

operator fun IOType.D2.plus(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] + other[i, j] }
