package com.wsr.collection

import com.wsr.IOType

fun IOType.D1.sum() = value.sum()

fun List<IOType.D1>.sum(): IOType.D1 = IOType.d1(first().shape[0]) {
    var sum = 0.0
    for (i in indices) {
        sum += this[i][it]
    }
    sum
}

fun IOType.D2.sum() = value.sum()

fun IOType.D2.sum(axis: Int): IOType.D1 = when (axis) {
    0 ->
        IOType.d1(shape[1]) {
            var sum = 0.0
            for (i in 0 until shape[0]) {
                sum += this[i, it]
            }
            sum
        }

    1 ->
        IOType.d1(shape[0]) {
            var sum = 0.0
            for (i in 0 until shape[1]) {
                sum += this[it, i]
            }
            sum
        }

    else -> throw IllegalArgumentException("IOType.D2.sum axis is $axis not 0 or 1.")
}

fun IOType.D3.sum() = value.sum()
