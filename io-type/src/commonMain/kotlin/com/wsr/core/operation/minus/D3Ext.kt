package com.wsr.core.operation.minus

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.zip.zipWith

operator fun IOType.D3.minus(other: Float): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] - other }

operator fun IOType.D3.minus(other: IOType.D0): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] - other.get() }

operator fun IOType.D3.minus(other: IOType.D2): IOType.D3 = this.minus(other = other, axis = 0)

fun IOType.D3.minus(other: IOType.D2, axis: Int): IOType.D3 = when (axis) {
    0 -> IOType.d3(shape) { i, j, k -> this[i, j, k] - other[j, k] }
    1 -> IOType.d3(shape) { i, j, k -> this[i, j, k] - other[i, k] }
    2 -> IOType.d3(shape) { i, j, k -> this[i, j, k] - other[i, j] }
    else -> throw IllegalArgumentException("IOType.D3.minus axis is $axis not 0, 1 or 2.")
}

operator fun IOType.D3.minus(other: IOType.D3): IOType.D3 = IOType.d3(shape) { i, j, k ->
    this[i, j, k] - other[i, j, k]
}

fun IOType.D3.minus2(other: IOType.D2, axis1: Int, axis2: Int) = zipWith(other, axis1, axis2) { a, b -> a - b }

