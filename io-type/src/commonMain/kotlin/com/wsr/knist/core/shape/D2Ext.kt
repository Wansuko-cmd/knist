package com.wsr.knist.core.shape

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.size
import com.wsr.knist.core.D2
import com.wsr.knist.core.D3
import com.wsr.knist.core.D4
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp

@ScopeOp
fun IOType.D2.broadcastToD3(axis: Int, size: Int): IOType.D3.Global = when (axis) {
    0 -> {
        val result = Backend.gather(x = DataBuffer.create(size), y = value, i = 1, j = 1, k = i * j)
        IOType.D3(shape = listOf(size, i, j), value = result)
    }

    1 -> {
        val result = Backend.gather(x = DataBuffer.create(size), y = value, i = i, j = 1, k = j)
        IOType.D3(shape = listOf(i, size, j), value = result)
    }

    2 -> {
        val result = Backend.gather(x = DataBuffer.create(size), y = value, i = i * j, j = 1, k = 1)
        IOType.D3(shape = listOf(i, j, size), value = result)
    }

    else -> throw IllegalArgumentException("IOType.D2.broadcastToD3 axis is $axis not 0, 1 or 2.")
}

@ScopeOp
fun IOType.D2.transpose(): IOType.D2.Global {
    val result = Backend.transpose(x = value, xi = i, xj = j)
    return IOType.D2(shape = listOf(j, i), value = result)
}

fun IOType.D2.reshapeToD3(i: Int, j: Int, k: Int): IOType.D3.Global = reshapeToD3(shape = listOf(i, j, k))

fun IOType.D2.reshapeToD3(shape: List<Int>): IOType.D3.Global = IOType.D3(shape = shape, value = value)

fun IOType.D2.reshapeToD4(i: Int, j: Int, k: Int, l: Int): IOType.D4.Global = IOType.D4(shape = listOf(i, j, k, l), value = value)

@ScopeOp
fun IOType.D2.slice(indices: IntProgression, axis: Int): IOType.D2.Global {
    val result = Backend.slice(x = value, xi = i, xj = j, axis = axis, indices = indices)
    return IOType.D2(
        shape = when (axis) {
            0 -> listOf(indices.size, j)
            else -> listOf(i, indices.size)
        },
        value = result,
    )
}

@ScopeOp
fun IOType.D2.interleave(other: IOType.D2, axis: Int): IOType.D2.Global {
    check(i == other.i && j == other.j)
    val result = DataBuffer.create(size + other.size)
    val newShape = when (axis) {
        0 -> listOf(i * 2, j)
        else -> listOf(i, j * 2)
    }
    val (i, j) = newShape
    Backend.copyInto(
        x = value,
        y = result,
        yi = i,
        yj = j,
        axis = axis,
        indices = when (axis) {
            0 -> 0 until i step 2
            else -> 0 until j step 2
        },
    )
    Backend.copyInto(
        x = other.value,
        y = result,
        yi = i,
        yj = j,
        axis = axis,
        indices = when (axis) {
            0 -> 1 until i step 2
            else -> 1 until j step 2
        },
    )
    return IOType.D2(shape = newShape, value = result)
}
