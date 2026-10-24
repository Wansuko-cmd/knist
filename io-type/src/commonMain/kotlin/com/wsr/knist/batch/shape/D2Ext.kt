package com.wsr.knist.batch.shape

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.size
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.d2
import com.wsr.knist.batch.d3
import com.wsr.knist.batch.get
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.core.D3
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@ScopeOp
fun Batch<IOType.D2>.broadcastToD3(axis: Int, size: Int): Batch<IOType.D3.Global> = when (axis) {
    0 -> {
        val result = Backend.gather(x = DataBuffer.create(size), y = value, i = this.size, j = 1, k = i * j)
        Batch.d3(this.size, size, i, j, result)
    }

    1 -> {
        val result = Backend.gather(x = DataBuffer.create(size), y = value, i = this.size * i, j = 1, k = j)
        Batch.d3(this.size, i, size, j, result)
    }

    2 -> {
        val result = Backend.gather(x = DataBuffer.create(size), y = value, i = this.size * i * j, j = 1, k = 1)
        Batch.d3(this.size, i, j, size, result)
    }

    else -> throw IllegalArgumentException("Batch<IOType.D2>.broadcastToD3 axis is $axis not 0, 1 or 2.")
}

fun Batch<IOType.D2>.toD3(): IOType.D3 = IOType.D3(shape = listOf(size, i, j), value = value)

fun IOType.D3.toBatch(): Batch<IOType.D2.Global> = Batch.d2(i, j, k, value)

@JvmName("batchD2sToList")
fun Batch<IOType.D2>.toList(): List<IOType.D2> = List(size) { get(it) }

@JvmName("batchD2sFlatten")
fun Batch<IOType.D2>.flatten() = Batch.d1(size, step, value)

@JvmName("batchD2sReshapeToD3")
fun Batch<IOType.D2>.reshapeToD3(i: Int, j: Int, k: Int) = reshapeToD3(listOf(i, j, k))

@JvmName("batchD2sReshapeToD3ByShape")
fun Batch<IOType.D2>.reshapeToD3(shape: List<Int>) = Batch.d3(size, shape, value)

@ScopeOp
fun Batch<IOType.D2>.slice(indices: IntProgression, axis: Int): Batch<IOType.D2.Global> {
    val result = Backend.slice(x = value, xi = size, xj = i, xk = j, axis = axis + 1, indices = indices)
    return Batch.d2(
        size,
        when (axis) {
            0 -> listOf(indices.size, j)
            else -> listOf(i, indices.size)
        },
        result,
    )
}

@ScopeOp
fun Batch<IOType.D2>.concat(other: Batch<IOType.D2>, axis: Int): Batch<IOType.D2.Global> = when (axis) {
    0 -> {
        val newI = i + other.i
        val result = DataBuffer.create(size * newI * j)
        Backend.copyInto(value, result, yi = size, yj = newI, yk = j, axis = 1, indices = 0 until i)
        Backend.copyInto(other.value, result, yi = size, yj = newI, yk = j, axis = 1, indices = i until newI)
        Batch.d2(size, newI, j, result)
    }

    1 -> {
        val newJ = j + other.j
        val result = DataBuffer.create(size * i * newJ)
        Backend.copyInto(value, result, yi = size * i, yj = newJ, axis = 1, indices = 0 until j)
        Backend.copyInto(other.value, result, yi = size * i, yj = newJ, axis = 1, indices = j until newJ)
        Batch.d2(size, i, newJ, result)
    }

    else -> throw IllegalArgumentException("Batch<IOType.D2>.concat axis is $axis, not 0 or 1.")
}

@ScopeOp
fun Batch<IOType.D2>.padding(axis: Int, left: Int, right: Int, @ScopeOpDefault("0f") fill: Float = 0f): Batch<IOType.D2.Global> {
    val newI = if (axis == 0) left + i + right else i
    val newJ = if (axis == 1) left + j + right else j
    val result = DataBuffer.create(FloatArray(size * newI * newJ) { fill })
    Backend.copyInto(
        x = value,
        y = result,
        yi = size,
        yj = newI,
        yk = newJ,
        axis = axis + 1,
        indices = left until left + if (axis == 0) i else j,
    )
    return Batch.d2(size = size, i = newI, j = newJ, value = result)
}

@ScopeOp
fun Batch<IOType.D2>.interleave(other: Batch<IOType.D2>, axis: Int): Batch<IOType.D2.Global> {
    check(size == other.size && i == other.i && j == other.j)
    val result = DataBuffer.create(value.size + other.value.size)
    val newShape = when (axis) {
        0 -> listOf(i * 2, j)
        else -> listOf(i, j * 2)
    }
    val (newI, newJ) = newShape
    Backend.copyInto(
        x = value,
        y = result,
        yi = size,
        yj = newI,
        yk = newJ,
        axis = axis + 1,
        indices = when (axis) {
            0 -> 0 until newI step 2
            else -> 0 until newJ step 2
        },
    )
    Backend.copyInto(
        x = other.value,
        y = result,
        yi = size,
        yj = newI,
        yk = newJ,
        axis = axis + 1,
        indices = when (axis) {
            0 -> 1 until newI step 2
            else -> 1 until newJ step 2
        },
    )
    return Batch.d2(size, newShape, result)
}

@ScopeOp
fun Batch<IOType.D2>.transpose(): Batch<IOType.D2.Global> {
    val result = Backend.transpose(x = value, xi = size, xj = i, xk = j, axisI = 0, axisJ = 2, axisK = 1)
    return Batch.d2(size, shape.reversed(), result)
}
