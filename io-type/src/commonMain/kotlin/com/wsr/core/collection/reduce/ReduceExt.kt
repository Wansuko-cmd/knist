package com.wsr.core.collection.reduce

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get

inline fun IOType.D2.reduce(axis: Int, operation: (Float, Float) -> Float) = when (axis) {
    0 -> IOType.d1(shape[1]) { j ->
        var acc = this[0, j]
        for (i in 1 until shape[0]) acc = operation(acc, this[i, j])
        acc
    }

    1 -> IOType.d1(shape[0]) { i ->
        var acc = this[i, 0]
        for (j in 1 until shape[1]) acc = operation(acc, this[i, j])
        acc
    }
    else -> throw IllegalArgumentException("IOType.D2.reduce axis is $axis not 0 or 1.")
}

inline fun IOType.D3.reduce(axis: Int, operation: (Float, Float) -> Float) = when (axis) {
    0 -> IOType.d2(listOf(shape[1], shape[2])) { j, k ->
        var acc = this[0, j, k]
        for (i in 1 until shape[0]) acc = operation(acc, this[i, j, k])
        acc
    }

    1 -> IOType.d2(listOf(shape[0], shape[2])) { i, k ->
        var acc = this[i, 0, k]
        for (j in 1 until shape[1]) acc = operation(acc, this[i, j, k])
        acc
    }

    2 -> IOType.d2(listOf(shape[0], shape[1])) { i, j ->
        var acc = this[i, j, 0]
        for (k in 1 until shape[2]) acc = operation(acc, this[i, j, k])
        acc
    }
    else -> throw IllegalArgumentException("IOType.D3.reduce axis is $axis not 0, 1 or 2.")
}
