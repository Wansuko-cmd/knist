package com.wsr.knist.core

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.core.elementwise.operation.times.times
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun IOType.Companion.d3Impl(i: Int, j: Int, k: Int, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): IOType.D3.Global {
    val value = FloatArray(i * j * k)
    for (_i in 0 until i) {
        for (_j in 0 until j) {
            for (_k in 0 until k) {
                value[(_i * j + _j) * k + _k] = init(_i, _j, _k)
            }
        }
    }
    return d3Impl(shape = listOf(i, j, k), value = value)
}

@PublishedApi
internal inline fun IOType.Companion.d3Impl(shape: List<Int>, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): IOType.D3.Global = d3Impl(
    i = shape[0],
    j = shape[1],
    k = shape[2],
    init = init,
)

internal fun IOType.Companion.d3Impl(shape: List<Int>, value: List<Float>): IOType.D3.Global = d3Impl(
    shape = shape,
    value = value.toFloatArray(),
)

@JvmName("d3ImplWithElements")
internal fun IOType.Companion.d3Impl(vararg elements: IOType.D2): IOType.D3.Global {
    val size = elements.size
    val shape = elements.first().shape
    val step = shape.reduce { acc, i -> acc * i }
    val value = DataBuffer.create(size * step)
    elements.forEachIndexed { index, item ->
        val start = index * step
        Backend.copyInto(item.value, value, start until start + item.size)
    }
    return IOType.D3.Global(shape = listOf(size, shape[0], shape[1]), value = value)
}

@PublishedApi
internal fun IOType.Companion.d3Impl(shape: List<Int>, value: FloatArray): IOType.D3.Global = IOType.D3.Global(shape = shape, value = DataBuffer.create(value))

operator fun IOType.D3.get(i: Int, j: Int, k: Int): IOType.D0 = IOType.d0(value[(i * shape[1] + j) * shape[2] + k])

operator fun IOType.D3.get(i: Int, j: Int): IOType.D1 {
    val offset = (i * shape[1] + j) * shape[2]
    val result = Backend.slice(x = value, indices = offset until offset + shape[2])
    return IOType.D1(value = result)
}

operator fun IOType.D3.get(i: Int): IOType.D2 {
    val offset = i * shape[1] * shape[2]
    val result = Backend.slice(x = value, indices = offset until offset + shape[1] * shape[2])
    return IOType.D2(
        shape = listOf(shape[1], shape[2]),
        value = result,
    )
}

operator fun IOType.D3.set(i: Int, j: Int, z: Int, element: Float) {
    value[(i * shape[1] + j) * shape[2] + z] = element
}

operator fun IOType.D3.set(i: Int, j: Int, element: IOType.D1) {
    val start = (i * shape[1] + j) * shape[2]
    Backend.copyInto(
        x = element.value,
        y = value,
        indices = start until start + element.value.size,
    )
}

operator fun IOType.D3.set(i: Int, element: IOType.D2) {
    val start = i * shape[1] * shape[2]
    Backend.copyInto(
        x = element.value,
        y = value,
        indices = start until start + element.value.size,
    )
}

@ScopeOp
fun IOType.D3.concat(other: IOType.D3, axis: Int): IOType.D3.Global = when (axis) {
    0 -> {
        val newI = i + other.i
        val result = DataBuffer.create(newI * j * k)
        Backend.copyInto(x = value, y = result, yi = newI, yj = j * k, axis = 0, indices = 0 until i)
        Backend.copyInto(x = other.value, y = result, yi = newI, yj = j * k, axis = 0, indices = i until newI)
        IOType.D3.Global(value = result, shape = listOf(newI, j, k))
    }

    1 -> {
        val newJ = j + other.j
        val result = DataBuffer.create(i * newJ * k)
        Backend.copyInto(x = value, y = result, yi = i, yj = newJ, yk = k, axis = 1, indices = 0 until j)
        Backend.copyInto(x = other.value, y = result, yi = i, yj = newJ, yk = k, axis = 1, indices = j until newJ)
        IOType.D3.Global(value = result, shape = listOf(i, newJ, k))
    }

    2 -> {
        val newK = k + other.k
        val result = DataBuffer.create(i * j * newK)
        Backend.copyInto(x = value, y = result, yi = i * j, yj = newK, axis = 1, indices = 0 until k)
        Backend.copyInto(x = other.value, y = result, yi = i * j, yj = newK, axis = 1, indices = k until newK)
        IOType.D3.Global(value = result, shape = listOf(i, j, newK))
    }

    else -> throw IllegalArgumentException("IOType.D3.concat axis is $axis, not 0, 1 or 2.")
}

@ScopeOp
operator fun IOType.D3.unaryMinus(): IOType.D3 = -1f * this
