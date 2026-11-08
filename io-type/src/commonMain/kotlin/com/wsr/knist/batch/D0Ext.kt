package com.wsr.knist.batch

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.batch.elementwise.operation.times.times
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d0
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun Batch.Companion.d0Impl(size: Int, init: (b: Int) -> Float = { 0f }): Batch<IOType.D0.Global> {
    val value = FloatArray(size)

    @Suppress("LocalVariableName")
    repeat(size) { _b ->
        value[_b] = init(_b)
    }

    return Batch.d0Impl(size = size, value = value)
}

@PublishedApi
internal fun Batch.Companion.d0Impl(size: Int, value: FloatArray): Batch<IOType.D0.Global> = Batch(value = DataBuffer.create(value), size = size, shape = listOf(1))

@PublishedApi
internal fun Batch.Companion.d0Impl(size: Int, value: DataBuffer): Batch<IOType.D0.Global> = Batch(value = value, size = size, shape = listOf(1))

@JvmName("batchD0sGet")
@ScopeOp
operator fun Batch<IOType.D0>.get(i: Int): IOType.D0 {
    val index = i * step
    return IOType.d0(value[index])
}

@ScopeOp
operator fun Batch<IOType.D0>.unaryMinus(): Batch<IOType.D0> = -1f * this
