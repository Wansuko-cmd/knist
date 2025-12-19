package com.wsr.core.operation.zip

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

inline fun IOType.D2.zipWith(other: IOType.D1, axis: Int, block: (Float, Float) -> Float): IOType.D2 = when (axis) {
    0 -> IOType.d2(shape) { i, j -> block(this[i, j], other[i]) }
    1 -> IOType.d2(shape) { i, j -> block(this[i, j], other[j]) }
    else -> throw IllegalArgumentException("IOType.D2.zipWith axis is $axis not 0 or 1.")
}

inline fun IOType.D2.zipWith(other: IOType.D3, axis1: Int, axis2: Int, block: (Float, Float) -> Float): IOType.D3 =
    when (axis1) {
        0 -> when (axis2) {
            1 -> IOType.d3(other.shape) { i, j, k -> block(this[i, j], other[i, j, k]) }
            2 -> IOType.d3(other.shape) { i, j, k -> block(this[i, k], other[i, j, k]) }
            else -> throw IllegalArgumentException("IOType.D2.zipWith axis2 is $axis2 not 1 or 2.")
        }

        1 -> when (axis2) {
            2 -> IOType.d3(other.shape) { i, j, k -> block(this[j, k], other[i, j, k]) }
            else -> throw IllegalArgumentException("IOType.D2.zipWith axis2 is $axis2 not 2.")
        }

        else -> throw IllegalArgumentException("IOType.D2.zipWith axis1 is $axis1 not 0 or 1.")
    }
