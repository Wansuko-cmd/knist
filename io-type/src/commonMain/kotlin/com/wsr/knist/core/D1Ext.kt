package com.wsr.knist.core

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.scope.ScopeOp

@PublishedApi
internal inline fun IOType.Companion.d1Impl(i: Int, init: (Int) -> Float = { 0f }): IOType.D1.Global {
    val value = FloatArray(i)
    for (_i in 0 until i) value[_i] = init(_i)
    return d1Impl(value = value)
}

@PublishedApi
internal inline fun IOType.Companion.d1Impl(shape: List<Int>, init: (Int) -> Float = { 0f }): IOType.D1.Global = d1Impl(shape[0], init)

internal fun IOType.Companion.d1Impl(value: List<Float>): IOType.D1.Global = d1Impl(value = value.toFloatArray())

@PublishedApi
internal fun IOType.Companion.d1Impl(value: FloatArray): IOType.D1.Global = IOType.D1.Global(value = DataBuffer.create(value))

operator fun IOType.D1.get(index: Int): IOType.D0 = IOType.d0(value[index])

operator fun IOType.D1.set(index: Int, element: Float) {
    value[index] = element
}

@ScopeOp
fun IOType.D1.concat(other: IOType.D1): IOType.D1.Global {
    val newI = i + other.i
    val result = DataBuffer.create(newI)
    Backend.copyInto(x = value, y = result, indices = 0 until i)
    Backend.copyInto(x = other.value, y = result, indices = i until newI)
    return IOType.D1.Global(value = result)
}
