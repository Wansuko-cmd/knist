package com.wsr.collection

import com.wsr.IOType
import com.wsr.d1
import com.wsr.d2
import com.wsr.get
import com.wsr.reshape.transpose

fun IOType.D1.sum() = value.sum()

fun IOType.D2.sum() = value.sum()

fun IOType.D2.sum(axis: Int): IOType.D1 = when (axis) {
    0 -> {
        val transpose = transpose()
        IOType.d1(shape[1]) { transpose[it].sum() }
    }

    1 -> {
        IOType.d1(shape[0]) { this[it].sum() }
    }

    else -> throw IllegalArgumentException("IOType.D2.sum axis is $axis not 0 or 1.")
}

fun IOType.D3.sum() = value.sum()

fun IOType.D3.sum(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose(axisI = 1, axisJ = 2, axisK = 0)
        IOType.d2(i = transpose.shape[1], j = transpose.shape[2]) { i, j -> transpose[i, j].sum() }
    }

    1 -> {
        val transpose = transpose(axisI = 0, axisJ = 2, axisK = 1)
        IOType.d2(i = transpose.shape[0], j = transpose.shape[1]) { i, j -> transpose[i, j].sum() }
    }

    2 -> {
        IOType.d2(i = shape[0], j = shape[1]) { i, j -> this[i, j].sum() }
    }

    else -> throw IllegalArgumentException("IOType.D3.sum axis is $axis not 0, 1 or 2.")
}
