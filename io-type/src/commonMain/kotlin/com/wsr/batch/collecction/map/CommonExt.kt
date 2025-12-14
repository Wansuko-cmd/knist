package com.wsr.batch.collecction.map

import com.wsr.base.DataBuffer
import com.wsr.batch.Batch
import com.wsr.core.IOType

inline fun <T : IOType> Batch<T>.mapValue(block: (Float) -> Float): Batch<T> {
    val result = Batch<T>(value = DataBuffer.create(value.toFloatArray().copyOf()), size = size, shape = shape)
    for (i in result.value.indices) result.value[i] = block(result.value[i])
    return result
}
