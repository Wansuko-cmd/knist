package com.wsr.core.operation.zip

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get

inline fun IOType.D3.zipWith(other: IOType.D1, axis: Int, block: (Float, Float) -> Float): IOType.D3 =
    when(axis) {
        0 -> IOType.d3(shape) { i, j, k -> block(this[i, j, k], other[i]) }
        1 -> IOType.d3(shape) { i, j, k -> block(this[i, j, k], other[j]) }
        2 -> IOType.d3(shape) { i, j, k -> block(this[i, j, k], other[k]) }
        else -> throw IllegalArgumentException("IOType.D3.zipWith axis is $axis not 0, 1 or 2.")
    }

inline fun IOType.D3.zipWith(other: IOType.D2, axis1: Int, axis2: Int, block: (Float, Float) -> Float): IOType.D3 =
    when (axis1) {
        0 -> when (axis2) {
            1 -> IOType.d3(shape) { i, j, k -> block(this[i, j, k], other[i, j]) }
            2 -> IOType.d3(shape) { i, j, k -> block(this[i, j, k], other[i, k]) }
            else -> throw IllegalArgumentException("IOType.D3.zipWith axis2 is $axis2 not 1 or 2.")
        }

        1 -> when (axis2) {
            2 -> IOType.d3(shape) { i, j, k -> block(this[i, j, k], other[j, k]) }
            else -> throw IllegalArgumentException("IOType.D3.zipWith axis2 is $axis2 not 2.")
        }

        else -> throw IllegalArgumentException("IOType.D3.zipWith axis1 is $axis1 not 0 or 1.")
    }
