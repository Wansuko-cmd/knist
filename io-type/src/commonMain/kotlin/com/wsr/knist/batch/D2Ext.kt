package com.wsr.knist.batch

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.batch.elementwise.operation.times.times
import com.wsr.knist.core.D2
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d2
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun Batch.Companion.d2Impl(size: Int, i: Int, j: Int, init: (Int, Int) -> Float = { _, _ -> 0f }): Batch<IOType.D2.Global> = Batch(size) { IOType.d2(i, j, init) }

@PublishedApi
internal inline fun Batch.Companion.d2Impl(size: Int, shape: List<Int>, init: (Int, Int) -> Float = { _, _ -> 0f }): Batch<IOType.D2.Global> = d2Impl(size, shape[0], shape[1], init)

internal fun Batch.Companion.d2Impl(size: Int, i: Int, j: Int, value: FloatArray): Batch<IOType.D2.Global> = Batch(value = DataBuffer.create(value), size = size, shape = listOf(i, j))

internal fun Batch.Companion.d2Impl(size: Int, i: Int, j: Int, value: DataBuffer): Batch<IOType.D2.Global> = Batch(value = value, size = size, shape = listOf(i, j))

internal fun Batch.Companion.d2Impl(size: Int, shape: List<Int>, value: DataBuffer): Batch<IOType.D2.Global> = Batch(value = value, size = size, shape = shape)

val Batch<IOType.D2>.i get() = shape[0]

val Batch<IOType.D2>.j get() = shape[1]

@JvmName("batchD2sGet")
@ScopeOp
operator fun Batch<IOType.D2>.get(i: Int): IOType.D2 {
    val index = i * step
    val result = Backend.slice(x = value, indices = index until index + step)
    return IOType.D2(shape = shape, value = result)
}

operator fun Batch<IOType.D2>.set(i: Int, element: IOType.D2) {
    val start = i * step
    Backend.copyInto(element.value, value, start until start + element.value.size)
}

@ScopeOp
operator fun Batch<IOType.D2>.unaryMinus(): Batch<IOType.D2> = -1f * this
