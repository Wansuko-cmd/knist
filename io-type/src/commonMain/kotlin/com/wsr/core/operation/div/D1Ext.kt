package com.wsr.core.operation.div

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get

operator fun IOType.D1.div(other: Float): IOType.D1 = IOType.d1(shape) { this[it] / other }

operator fun IOType.D1.div(other: IOType.D0): IOType.D1 = IOType.d1(shape) { this[it] / other.get() }

operator fun IOType.D1.div(other: IOType.D1): IOType.D1 = IOType.d1(this.shape) { i -> this[i] / other[i] }

operator fun IOType.D1.div(other: IOType.D2): IOType.D2 = div(other = other, axis = 0)

fun IOType.D1.div(other: IOType.D2, axis: Int): IOType.D2 = when (axis) {
    0 -> IOType.d2(other.shape) { i, j -> this[j] / other[i, j] }
    1 -> IOType.d2(other.shape) { i, j -> this[i] / other[i, j] }
    else -> throw IllegalArgumentException("IOType.D1.div axis is $axis not 0 or 1.")
}

fun IOType.D1.div2(other: IOType.D2, axis: Int): IOType.D2 = when (axis) {
    0 -> IOType.d2(other.shape) { i, j -> this[i] / other[i, j] }
    1 -> IOType.d2(other.shape) { i, j -> this[j] / other[i, j] }
    else -> throw IllegalArgumentException("IOType.D1.div axis is $axis not 0 or 1.")
}
