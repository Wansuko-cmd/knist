package com.wsr.d2

import com.wsr.IOType

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

fun IOType.D2.sum() = value.sum()
