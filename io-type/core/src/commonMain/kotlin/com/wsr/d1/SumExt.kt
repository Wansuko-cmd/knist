package com.wsr.d1

import com.wsr.IOType

fun List<IOType.D1>.sum(): IOType.D1 = IOType.d1(first().shape[0]) {
    var sum = 0.0
    for (i in indices) {
        sum += this[i][it]
    }
    sum
}

fun IOType.D1.sum() = value.sum()
