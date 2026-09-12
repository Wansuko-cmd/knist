package com.wsr.knist.batch

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d0
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun Batch.Companion.d0Impl(size: Int, init: (Int) -> Float = { 0f }): Batch<IOType.D0.Global> = Batch(size) { IOType.d0(init(it)) }

internal fun Batch.Companion.d0Impl(size: Int, value: DataBuffer): Batch<IOType.D0.Global> = Batch(value = value, size = size, shape = listOf(1))

@JvmName("batchD0sGet")
@ScopeOp
operator fun Batch<IOType.D0>.get(i: Int): IOType.D0 {
    val index = i * step
    return IOType.d0(value[index])
}

operator fun Batch<IOType.D0>.set(i: Int, element: IOType.D0) {
    value[i] = element.value[0]
}
