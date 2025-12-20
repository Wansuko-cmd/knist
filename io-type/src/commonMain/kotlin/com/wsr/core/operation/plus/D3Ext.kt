package com.wsr.core.operation.plus

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.zip.zipWith

operator fun IOType.D3.plus(other: Float): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] + other }

operator fun IOType.D3.plus(other: IOType.D0): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] + other.get() }

fun IOType.D3.plus(other: IOType.D2, axis1: Int, axis2: Int) = zipWith(other, axis1, axis2) { a, b -> a + b }

operator fun IOType.D3.plus(other: IOType.D3): IOType.D3 = IOType.d3(shape) { i, j, k ->
    this[i, j, k] + other[i, j, k]
}
