package com.wsr.collection

import com.wsr.BLAS
import com.wsr.IOType
import com.wsr.reshape.transpose

fun IOType.D1.average(): Double = value.average()

@JvmName("averageToD1s")
fun List<IOType.D1>.average(): List<Double> = map { it.average() }

fun IOType.D2.average(): Double = value.average()

@JvmName("averageToD2s")
fun List<IOType.D2>.average(): List<Double> = map { it.average() }

fun IOType.D2.average(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose()
        IOType.d1(shape[1]) { transpose[it].average() }
    }

    1 -> IOType.d1(shape[0]) { this[it].average() }
    else -> throw IllegalArgumentException("IOType.D2.max axis is $axis not 0 or 1.")
}

@JvmName("averageAxisToD2s")
fun List<IOType.D2>.average(axis: Int) = map { it.average(axis) }

fun IOType.D3.average(): Double = value.average()

@JvmName("averageToD3s")
fun List<IOType.D3>.average(): List<Double> = map { it.average() }

fun IOType.D3.average(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose(1, 2, 0)
        IOType.d2(shape[1], shape[2]) { y, z -> transpose[y, z].average() }
    }

    1 -> {
        val transpose = transpose(0, 2, 1)
        IOType.d2(shape[0], shape[2]) { x, z -> transpose[x, z].average() }
    }

    2 -> IOType.d2(shape[0], shape[1]) { x, y -> this[x, y].average() }
    else -> throw IllegalArgumentException("IOType.D3.max axis is $axis not 0, 1 or 2.")
}

@JvmName("averageAxisToD3s")
fun List<IOType.D3>.average(axis: Int) = map { it.average(axis) }
