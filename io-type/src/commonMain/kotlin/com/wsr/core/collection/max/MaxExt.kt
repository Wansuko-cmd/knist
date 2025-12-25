package com.wsr.core.collection.max

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.collection.reduce.reduce

fun IOType.D1.max() = Backend.max(x = value)

fun IOType.D2.max() = Backend.max(x = value)

fun IOType.D2.max(axis: Int): IOType.D1 {
    val result = Backend.max(x = value, xi = i, xj = j, axis = axis)
    return IOType.D1(result)
}

fun IOType.D3.max() = Backend.max(x = value)

fun IOType.D3.max(axis: Int): IOType.D2 {
    val result = Backend.max(x = value, xi = i, xj = j, xk = k, axis = axis)
    return IOType.D2(
        shape = when (axis) {
            0 -> listOf(j, k)
            1 -> listOf(i, k)
            else -> listOf(i, j)
        },
        value = result,
    )
}
