package com.wsr.collection

import com.wsr.IOType
import com.wsr.reshape.transpose

fun IOType.D1.min() = value.min()

@JvmName("minToD1s")
fun List<IOType.D1>.min() = map { it.min() }

fun IOType.D2.min() = value.min()

@JvmName("minToD2s")
fun List<IOType.D2>.min() = map { it.min() }

fun IOType.D2.min(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose()
        IOType.d1(shape[1]) { transpose[it].min() }
    }

    1 -> IOType.d1(shape[0]) { this[it].min() }
    else -> throw IllegalArgumentException("IOType.D2.min axis is $axis not 0 or 1.")
}

@JvmName("minAxisToD2s")
fun List<IOType.D2>.min(axis: Int) = map { it.min(axis) }

fun IOType.D3.min() = value.min()

@JvmName("minToD3s")
fun List<IOType.D3>.min() = map { it.min() }

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

@JvmName("minAxisToD3s")
fun List<IOType.D3>.min(axis: Int) = map { it.min(axis) }
