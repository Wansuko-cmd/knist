package com.wsr.sum

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

fun IOType.D2.sum(axis: Int): IOType.D1 {
    val outputSize = if (axis == 0) shape[1] else shape[0]
    return IOType.d1(outputSize) {
        var sum = 0.0
        for (i in 0 until shape[axis]) {
            sum += this[i, it]
        }
        sum
    }
}
