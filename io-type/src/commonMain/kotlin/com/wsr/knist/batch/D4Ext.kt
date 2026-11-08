package com.wsr.knist.batch

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.core.D4
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d4
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun Batch.Companion.d4Impl(
    size: Int,
    i: Int,
    j: Int,
    k: Int,
    l: Int,
    init: (b: Int, i: Int, j: Int, k: Int, l: Int) -> Float = { _, _, _, _, _ -> 0f },
): Batch<IOType.D4.Global> {
    val value = FloatArray(size * i * j * k * l)

    @Suppress("LocalVariableName")
    repeat(size) { _b ->
        repeat(i) { _i ->
            repeat(j) { _j ->
                repeat(k) { _k ->
                    repeat(l) { _l ->
                        value[(((_b * i + _i) * j + _j) * k + _k) * l + _l] = init(_b, _i, _j, _k, _l)
                    }
                }
            }
        }
    }

    return Batch.d4Impl(size = size, i = i, j = j, k = k, l = l, value = value)
}

@PublishedApi
internal inline fun Batch.Companion.d4Impl(
    size: Int,
    shape: List<Int>,
    init: (b: Int, i: Int, j: Int, k: Int, l: Int) -> Float = { _, _, _, _, _ -> 0f },
): Batch<IOType.D4.Global> = d4Impl(size, shape[0], shape[1], shape[2], shape[3], init)

@PublishedApi
internal fun Batch.Companion.d4Impl(size: Int, i: Int, j: Int, k: Int, l: Int, value: FloatArray): Batch<IOType.D4.Global> = Batch(value = DataBuffer.create(value), size = size, shape = listOf(i, j, k, l))

@PublishedApi
internal fun Batch.Companion.d4Impl(size: Int, i: Int, j: Int, k: Int, l: Int, value: DataBuffer): Batch<IOType.D4.Global> = Batch(value = value, size = size, shape = listOf(i, j, k, l))

internal fun Batch.Companion.d4Impl(size: Int, shape: List<Int>, value: DataBuffer): Batch<IOType.D4.Global> = Batch(value = value, size = size, shape = shape)

val Batch<IOType.D4>.i get() = shape[0]

val Batch<IOType.D4>.j get() = shape[1]

val Batch<IOType.D4>.k get() = shape[2]

val Batch<IOType.D4>.l get() = shape[3]

@JvmName("batchD4sGet")
@ScopeOp
operator fun Batch<IOType.D4>.get(i: Int): IOType.D4 {
    val index = i * step
    val result = Backend.slice(x = value, indices = index until index + step)
    return IOType.D4(shape = shape, value = result)
}
