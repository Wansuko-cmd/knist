package com.wsr.knist.core

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.core.elementwise.operation.times.times
import com.wsr.knist.scope.ScopeOp

internal fun IOType.Companion.d0Impl(value: FloatArray): IOType.D0.Global = IOType.D0.Global(value = DataBuffer.create(value))

internal fun IOType.Companion.d0Impl(value: Float): IOType.D0.Global = d0Impl(floatArrayOf(value))

fun IOType.D0.unwrap() = value[0]

fun IOType.D0.set(element: Float) {
    value[0] = element
}

@ScopeOp
operator fun IOType.D0.unaryMinus(): IOType.D0 = -1f * this
