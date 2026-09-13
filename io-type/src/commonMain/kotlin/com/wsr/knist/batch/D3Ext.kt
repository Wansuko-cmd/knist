package com.wsr.knist.batch

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.batch.elementwise.operation.times.times
import com.wsr.knist.core.D3
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d3
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun Batch.Companion.d3Impl(size: Int, i: Int, j: Int, k: Int, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): Batch<IOType.D3.Global> = Batch(size) { IOType.d3(i, j, k, init) }

@PublishedApi
internal inline fun Batch.Companion.d3Impl(size: Int, shape: List<Int>, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): Batch<IOType.D3.Global> = d3Impl(size, shape[0], shape[1], shape[2], init)

internal fun Batch.Companion.d3Impl(size: Int, i: Int, j: Int, k: Int, value: DataBuffer): Batch<IOType.D3.Global> = Batch(value = value, size = size, shape = listOf(i, j, k))

internal fun Batch.Companion.d3Impl(size: Int, shape: List<Int>, value: DataBuffer): Batch<IOType.D3.Global> = Batch(value = value, size = size, shape = shape)

val Batch<IOType.D3>.i get() = shape[0]

val Batch<IOType.D3>.j get() = shape[1]

val Batch<IOType.D3>.k get() = shape[2]

@JvmName("batchD3sGet")
@ScopeOp
operator fun Batch<IOType.D3>.get(i: Int): IOType.D3 {
    val index = i * step
    val result = Backend.slice(x = value, indices = index until index + step)
    return IOType.D3(shape = shape, value = result)
}

operator fun Batch<IOType.D3>.set(i: Int, element: IOType.D3) {
    val start = i * step
    Backend.copyInto(element.value, value, start until start + element.value.size)
}

@ScopeOp
operator fun Batch<IOType.D3>.unaryMinus(): Batch<IOType.D3> = -1f * this
