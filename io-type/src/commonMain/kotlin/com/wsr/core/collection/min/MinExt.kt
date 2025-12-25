package com.wsr.core.collection.min

import com.wsr.Backend
import com.wsr.core.IOType

fun IOType.D1.min() = Backend.min(x = value)

fun IOType.D2.min() = Backend.min(x = value)

fun IOType.D2.min(axis: Int): IOType.D1 {
    val result = Backend.min(x = value, xi = i, xj = j, axis = axis)
    return IOType.D1(result)
}

fun IOType.D3.min() = Backend.min(x = value)

fun IOType.D3.min(axis: Int): IOType.D2 {
    val result = Backend.min(x = value, xi = i, xj = j, xk = k, axis = axis)
    return IOType.D2(
        shape = when (axis) {
            0 -> listOf(j, k)
            1 -> listOf(i, k)
            else -> listOf(i, j)
        },
        value = result,
    )
}
