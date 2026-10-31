package com.wsr.knist.core.shape

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.core.D1
import com.wsr.knist.core.D2
import com.wsr.knist.core.D3
import com.wsr.knist.core.D4
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp

@ScopeOp
fun IOType.D1.broadcastToD2(axis: Int, i: Int): IOType.D2.Global = when (axis) {
    0 -> {
        val result = Backend.gather(x = DataBuffer.create(i), y = value, i = 1, j = 1, k = this.i)
        IOType.D2(shape = listOf(i, this.i), value = result)
    }

    1 -> {
        val result = Backend.gather(x = DataBuffer.create(i), y = value, i = this.i, j = 1, k = 1)
        IOType.D2(shape = listOf(this.i, i), value = result)
    }

    else -> throw IllegalArgumentException("IOType.D1.broadcastToD2 axis is $axis not 0 or 1.")
}

fun IOType.D1.reshapeToD1(): IOType.D1 = IOType.D1(value = value)

fun IOType.D1.reshapeToD2(i: Int, j: Int): IOType.D2 = reshapeToD2(listOf(i, j))

fun IOType.D1.reshapeToD2(shape: List<Int>): IOType.D2 = IOType.D2(shape = shape, value = value)

fun IOType.D1.reshapeToD3(i: Int, j: Int, k: Int): IOType.D3 = reshapeToD3(listOf(i, j, k))

fun IOType.D1.reshapeToD3(shape: List<Int>): IOType.D3 = IOType.D3(shape = shape, value = value)

fun IOType.D1.reshapeToD4(i: Int, j: Int, k: Int, l: Int): IOType.D4 = reshapeToD4(listOf(i, j, k, l))

fun IOType.D1.reshapeToD4(shape: List<Int>): IOType.D4 = IOType.D4(shape = shape, value = value)

@ScopeOp
fun IOType.D1.slice(indices: IntProgression): IOType.D1.Global {
    val result = Backend.slice(x = value, indices = indices)
    return IOType.D1(value = result)
}

@ScopeOp
fun IOType.D1.interleave(other: IOType.D1): IOType.D1.Global {
    require(i == other.i)

    val result = DataBuffer.create(i + other.i)
    Backend.copyInto(x = value, y = result, indices = 0 until i * 2 step 2)
    Backend.copyInto(x = other.value, y = result, indices = 1 until i * 2 step 2)

    return IOType.D1(result)
}
