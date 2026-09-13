package com.wsr.knist.batch

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.batch.elementwise.operation.times.times
import com.wsr.knist.core.D1
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun Batch.Companion.d1Impl(size: Int, i: Int, init: (Int) -> Float = { 0f }): Batch<IOType.D1.Global> = Batch(size) { IOType.d1(i, init) }

@PublishedApi
internal inline fun Batch.Companion.d1Impl(size: Int, shape: List<Int>, init: (Int) -> Float = { 0f }): Batch<IOType.D1.Global> = d1Impl(size, shape[0], init)

internal fun Batch.Companion.d1Impl(size: Int, i: Int, value: FloatArray): Batch<IOType.D1.Global> = Batch(value = DataBuffer.create(value), size = size, shape = listOf(i))

internal fun Batch.Companion.d1Impl(size: Int, i: Int, value: DataBuffer): Batch<IOType.D1.Global> = Batch(value = value, size = size, shape = listOf(i))

internal fun Batch.Companion.d1Impl(size: Int, shape: List<Int>, value: DataBuffer): Batch<IOType.D1.Global> = Batch(value = value, size = size, shape = shape)

val Batch<IOType.D1>.i get() = shape[0]

@JvmName("batchD1sGet")
@ScopeOp
operator fun Batch<IOType.D1>.get(i: Int): IOType.D1 {
    val index = i * step
    val result = Backend.slice(x = value, indices = index until index + step)
    return IOType.D1(result)
}

operator fun Batch<IOType.D1>.set(i: Int, element: IOType.D1) {
    val start = i * step
    Backend.copyInto(element.value, value, start until start + element.value.size)
}

@ScopeOp
operator fun Batch<IOType.D1>.unaryMinus(): Batch<IOType.D1> = -1f * this
