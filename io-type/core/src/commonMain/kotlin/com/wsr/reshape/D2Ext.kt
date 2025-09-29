package com.wsr.reshape

import com.wsr.IOType

fun List<IOType.D2>.toD3(): IOType.D3 {
    var value = arrayOf<Double>()
    for (i in indices) {
        value += this[i].value
    }
    return IOType.d3(
        shape = listOf(size, first().shape[0], first().shape[1]),
        value = value,
    )
}

fun IOType.D2.transpose() = IOType.d2(shape.reversed()) { x, y -> this[y, x] }

fun List<IOType.D2>.transpose() = this.map { it.transpose() }
