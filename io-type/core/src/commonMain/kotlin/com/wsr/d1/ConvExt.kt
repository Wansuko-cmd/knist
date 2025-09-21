package com.wsr.d1

import com.wsr.IOType

fun IOType.D1.convD1(filter: IOType.D1): IOType.D1 {
    val outputSize = shape[0] - filter.shape[0] + 1
    return IOType.d1(outputSize) {
        var sum = 0.0
        for (k in 0 until filter.shape[0]) {
            sum += this[it + k] * filter[k]
        }
        sum
    }
}