package com.wsr.batch.collecction.map

import com.wsr.batch.Batch
import com.wsr.core.IOType

inline fun <T : IOType> Batch<T>.mapValue(block: (Float) -> Float): Batch<T> {
    val result = copy()
    for (i in result.value.indices) result.value[i] = block(result.value[i])
    return result
}
