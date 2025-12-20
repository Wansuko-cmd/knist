package com.wsr.core.operation.minus

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.operation.zip.zipWith

operator fun IOType.D2.minus(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other }

operator fun IOType.D2.minus(other: IOType.D0): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other.get() }

fun IOType.D2.minus(other: IOType.D1, axis: Int): IOType.D2 = zipWith(other, axis) { a, b -> a - b }

operator fun IOType.D2.minus(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other[i, j] }

fun IOType.D2.minus(other: IOType.D3, axis1: Int, axis2: Int) = zipWith(other, axis1, axis2) { a, b -> a - b }
