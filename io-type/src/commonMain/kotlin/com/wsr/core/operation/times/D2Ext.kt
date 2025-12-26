package com.wsr.core.operation.times

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.operation.zip.zipWith

operator fun IOType.D2.times(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other }

fun IOType.D2.times(other: IOType.D1, axis: Int): IOType.D2 = zipWith(other, axis) { a, b -> a * b }

operator fun IOType.D2.times(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other[i, j] }

fun IOType.D2.times(other: IOType.D3, axis1: Int, axis2: Int): IOType.D3 = zipWith(other, axis1, axis2) { a, b ->
    a * b
}
