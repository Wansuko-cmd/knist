package com.wsr.core.operation.plus

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get

operator fun IOType.D1.plus(other: Float): IOType.D1 = IOType.d1(shape) { this[it] + other }

operator fun IOType.D1.plus(other: IOType.D0): IOType.D1 = IOType.d1(shape) { this[it] + other.get() }

operator fun IOType.D1.plus(other: IOType.D1): IOType.D1 = IOType.d1(shape) { this[it] + other[it] }

fun IOType.D1.plus2(other: IOType.D2, axis: Int): IOType.D2 = when (axis) {
    0 -> IOType.d2(other.shape) { i, j -> this[i] + other[i, j] }
    1 -> IOType.d2(other.shape) { i, j -> this[j] + other[i, j] }
    else -> throw IllegalArgumentException("IOType.D1.plus axis is $axis not 0 or 1.")
}
