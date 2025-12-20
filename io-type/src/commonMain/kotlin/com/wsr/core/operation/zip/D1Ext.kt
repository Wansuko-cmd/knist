package com.wsr.core.operation.zip

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

inline fun IOType.D1.zipWith(other: IOType.D2, axis: Int, block: (Float, Float) -> Float): IOType.D2 = when (axis) {
    0 -> IOType.d2(other.shape) { i, j -> block(this[i], other[i, j]) }
    1 -> IOType.d2(other.shape) { i, j -> block(this[j], other[i, j]) }
    else -> throw IllegalArgumentException("IOType.D1.zipWith axis is $axis not 0 or 1.")
}

inline fun IOType.D1.zipWith(other: IOType.D3, axis: Int, block: (Float, Float) -> Float): IOType.D3 = when (axis) {
    0 -> IOType.d3(shape) { i, j, k -> block(this[i], other[i, j, k]) }
    1 -> IOType.d3(shape) { i, j, k -> block(this[j], other[i, j, k]) }
    2 -> IOType.d3(shape) { i, j, k -> block(this[k], other[i, j, k]) }
    else -> throw IllegalArgumentException("IOType.D3.zipWith axis is $axis not 0, 1 or 2.")
}
