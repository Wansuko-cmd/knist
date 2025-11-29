package com.wsr.core.collection.max

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.reshape.transpose.transpose

fun IOType.D1.max() = value.max()

fun IOType.D2.max() = value.max()

fun IOType.D2.max(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose()
        IOType.d1(shape[1]) { transpose[it].max() }
    }

    1 -> IOType.d1(shape[0]) { this[it].max() }
    else -> throw IllegalArgumentException("IOType.D2.max axis is $axis not 0 or 1.")
}

fun IOType.D3.max() = value.max()

fun IOType.D3.max(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose(1, 2, 0)
        IOType.d2(shape[1], shape[2]) { y, z -> transpose[y, z].max() }
    }

    1 -> {
        val transpose = transpose(0, 2, 1)
        IOType.d2(shape[0], shape[2]) { x, z -> transpose[x, z].max() }
    }

    2 -> IOType.d2(shape[0], shape[1]) { x, y -> this[x, y].max() }
    else -> throw IllegalArgumentException("IOType.D3.max axis is $axis not 0, 1 or 2.")
}
