package com.wsr.core.collection.min

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.reshape.transpose.transpose

fun IOType.D1.min() = value.min()

fun IOType.D2.min() = value.min()

fun IOType.D2.min(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose()
        IOType.d1(shape[1]) { transpose[it].min() }
    }

    1 -> IOType.d1(shape[0]) { this[it].min() }
    else -> throw IllegalArgumentException("IOType.D2.min axis is $axis not 0 or 1.")
}

fun IOType.D3.min() = value.min()

fun IOType.D3.min(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose(1, 2, 0)
        IOType.d2(shape[1], shape[2]) { y, z -> transpose[y, z].min() }
    }

    1 -> {
        val transpose = transpose(0, 2, 1)
        IOType.d2(shape[0], shape[2]) { x, z -> transpose[x, z].min() }
    }

    2 -> IOType.d2(shape[0], shape[1]) { x, y -> this[x, y].min() }
    else -> throw IllegalArgumentException("IOType.D3.min axis is $axis not 0, 1 or 2.")
}
