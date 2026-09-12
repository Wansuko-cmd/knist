package com.wsr.knist.batch

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.core.D4
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d4
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun Batch.Companion.d4Impl(size: Int, i: Int, j: Int, k: Int, l: Int, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }): Batch<IOType.D4.Global> =
    Batch(size) { IOType.d4(i, j, k, l, init) }

@PublishedApi
internal inline fun Batch.Companion.d4Impl(size: Int, shape: List<Int>, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }): Batch<IOType.D4.Global> =
    d4Impl(size, shape[0], shape[1], shape[2], shape[3], init)

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

operator fun Batch<IOType.D4>.set(i: Int, element: IOType.D4) {
    val start = i * step
    Backend.copyInto(element.value, value, start until start + element.value.size)
}
