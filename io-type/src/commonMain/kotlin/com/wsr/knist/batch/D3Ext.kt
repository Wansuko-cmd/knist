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
internal inline fun Batch.Companion.d3Impl(
    size: Int,
    i: Int,
    j: Int,
    k: Int,
    init: (b: Int, i: Int, j: Int, k: Int) -> Float = { _, _, _, _ -> 0f },
): Batch<IOType.D3.Global> {
    val value = FloatArray(size * i * j * k)

    @Suppress("LocalVariableName")
    repeat(size) { _b ->
        repeat(i) { _i ->
            repeat(j) { _j ->
                repeat(k) { _k ->
                    value[((_b * i + _i) * j + _j) * k + _k] = init(_b, _i, _j, _k)
                }
            }
        }
    }

    return Batch.d3Impl(size = size, i = i, j = j, k = k, value = value)
}

@PublishedApi
internal inline fun Batch.Companion.d3Impl(
    size: Int,
    shape: List<Int>,
    init: (b: Int, i: Int, j: Int, k: Int) -> Float = { _, _, _, _ -> 0f },
): Batch<IOType.D3.Global> = d3Impl(size, shape[0], shape[1], shape[2], init)

@PublishedApi
internal fun Batch.Companion.d3Impl(size: Int, i: Int, j: Int, k: Int, value: FloatArray): Batch<IOType.D3.Global> = Batch(value = DataBuffer.create(value), size = size, shape = listOf(i, j, k))

@PublishedApi
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

@ScopeOp
operator fun Batch<IOType.D3>.unaryMinus(): Batch<IOType.D3> = -1f * this
