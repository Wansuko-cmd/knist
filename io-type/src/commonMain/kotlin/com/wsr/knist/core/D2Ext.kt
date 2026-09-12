package com.wsr.knist.core

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun IOType.Companion.d2Impl(i: Int, j: Int, init: (Int, Int) -> Float = { _, _ -> 0f }): IOType.D2.Global {
    val value = FloatArray(i * j)
    for (_i in 0 until i) {
        for (_j in 0 until j) {
            value[_i * j + _j] = init(_i, _j)
        }
    }
    return d2Impl(shape = listOf(i, j), value = value)
}

@PublishedApi
internal inline fun IOType.Companion.d2Impl(shape: List<Int>, init: (Int, Int) -> Float = { _, _ -> 0f }): IOType.D2.Global = d2Impl(
    i = shape[0],
    j = shape[1],
    init = init,
)

internal fun IOType.Companion.d2Impl(shape: List<Int>, value: List<Float>): IOType.D2.Global = d2Impl(
    shape = shape,
    value = value.toFloatArray(),
)

@JvmName("d2ImplWithElements")
internal fun IOType.Companion.d2Impl(vararg elements: IOType.D1): IOType.D2.Global {
    val size = elements.size
    val shape = elements.first().shape
    val step = shape.reduce { acc, i -> acc * i }
    val value = DataBuffer.create(size * step)
    elements.forEachIndexed { index, item ->
        val start = index * step
        Backend.copyInto(item.value, value, start until start + item.size)
    }
    return IOType.D2.Global(shape = listOf(size, shape[0]), value = value)
}

@PublishedApi
internal fun IOType.Companion.d2Impl(shape: List<Int>, value: FloatArray): IOType.D2.Global = IOType.D2.Global(shape = shape, value = DataBuffer.create(value))

operator fun IOType.D2.get(i: Int, j: Int): IOType.D0 = IOType.d0(value[i * shape[1] + j])

operator fun IOType.D2.get(i: Int): IOType.D1 {
    val offset = i * shape[1]
    val result = Backend.slice(x = value, indices = offset until offset + shape[1])
    return IOType.D1(result)
}

operator fun IOType.D2.set(i: Int, j: Int, element: Float) {
    value[i * shape[1] + j] = element
}

operator fun IOType.D2.set(i: Int, element: IOType.D1) {
    val start = i * shape[1]
    Backend.copyInto(
        x = element.value,
        y = value,
        indices = start until start + element.value.size,
    )
}

@ScopeOp
fun IOType.D2.concat(other: IOType.D2, axis: Int): IOType.D2.Global = when (axis) {
    0 -> {
        val newI = i + other.i
        val result = DataBuffer.create(newI * j)
        Backend.copyInto(x = value, y = result, yi = newI, yj = j, axis = 0, indices = 0 until i)
        Backend.copyInto(x = other.value, y = result, yi = newI, yj = j, axis = 0, indices = i until newI)
        IOType.D2.Global(value = result, shape = listOf(newI, j))
    }

    1 -> {
        val newJ = j + other.j
        val result = DataBuffer.create(i * newJ)
        Backend.copyInto(x = value, y = result, yi = i, yj = newJ, axis = 1, indices = 0 until j)
        Backend.copyInto(x = other.value, y = result, yi = i, yj = newJ, axis = 1, indices = j until newJ)
        IOType.D2.Global(value = result, shape = listOf(i, newJ))
    }

    else -> throw IllegalArgumentException("IOType.D2.concat axis is $axis, not 0 or 1.")
}
