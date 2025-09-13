package com.wsr.d1

import com.wsr.IOType

fun List<IOType.D1>.toD2(): IOType.D2 {
    var value = arrayOf<Double>()
    for (i in indices) {
        value += this[i].value
    }
    return IOType.d2(
        shape = listOf(size, first().shape[0]),
        value = value,
    )
}
