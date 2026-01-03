package com.wsr.core.collection.average

import com.wsr.Backend
import com.wsr.core.IOType

fun IOType.D1.average(): Float = Backend.average(value)

fun IOType.D2.average(): Float = Backend.average(value)

fun IOType.D2.average(axis: Int): IOType.D1 {
    val result = Backend.average(x = value, xi = i, xj = j, axis = axis)
    return IOType.D1(value = result)
}

fun IOType.D3.average(): Float = Backend.average(value)

fun IOType.D3.average(axis: Int): IOType.D2 {
    val result = Backend.average(x = value, xi = i, xj = j, xk = k, axis = axis)
    return IOType.D2(
        shape = when (axis) {
            0 -> listOf(j, k)
            1 -> listOf(i, k)
            else -> listOf(i, j)
        },
        value = result,
    )
}
