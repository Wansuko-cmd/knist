package com.wsr.batch.collecction.map

import com.wsr.base.DataBuffer
import com.wsr.batch.Batch
import com.wsr.core.IOType

inline fun <T : IOType> Batch<T>.mapValue(block: (Float) -> Float): Batch<T> {
    val result = Batch<T>(value = DataBuffer.create(value.size), size = size, shape = shape)
    for (i in result.value.indices) result.value[i] = block(value[i])
    return result
}

inline fun <T : IOType> Batch<T>.mapWith(other: T, block: (Float, Float) -> Float): Batch<T> {
    val result = Batch<T>(
        value = DataBuffer.create(value.size),
        size = size,
        shape = shape,
    )
    for (batch in 0 until size) {
        val offset = batch * step
        for (i in 0 until step) {
            result.value[offset + i] = block(value[offset + i], other.value[i])
        }
    }
    return result
}

inline fun <T : IOType> Batch<T>.mapWith(other: Batch<T>, block: (Float, Float) -> Float): Batch<T> {
    val result = Batch<T>(
        value = DataBuffer.create(value.size),
        size = size,
        shape = shape,
    )
    for (i in result.value.indices) result.value[i] = block(value[i], other.value[i])
    return result
}
