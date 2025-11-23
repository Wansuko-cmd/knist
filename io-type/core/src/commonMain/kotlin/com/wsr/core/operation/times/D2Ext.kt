package com.wsr.core.operation.times

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.operation.div.div

operator fun IOType.D2.times(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other }

operator fun IOType.D2.times(other: IOType.D1): IOType.D2 = this.times(other = other, axis = 0)

fun IOType.D2.times(other: IOType.D1, axis: Int): IOType.D2 = when (axis) {
    0 -> IOType.d2(shape) { i, j -> this[i, j] * other[j] }
    1 -> IOType.d2(shape) { i, j -> this[i, j] * other[i] }
    else -> throw IllegalArgumentException("IOType.D2.times axis is $axis not 0 or 1.")
}

operator fun IOType.D2.times(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other[i, j] }
