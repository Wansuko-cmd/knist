package com.wsr.d1

import com.wsr.IOType

fun List<IOType.D1>.toD2(): IOType.D2 {
    val destination = ArrayList<Double>()
    for (element in this) {
        destination.addAll(element.value)
    }
    return IOType.d2(
        shape = listOf(size, first().shape[0]),
        value = destination,
    )
}
