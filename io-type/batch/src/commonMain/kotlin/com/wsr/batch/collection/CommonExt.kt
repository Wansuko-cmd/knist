package com.wsr.batch.collection

import com.wsr.Batch
import com.wsr.IOType

inline fun <T: IOType> Batch<T>.mapValue(block: (Float) -> Float): Batch<T> {
    val result = copy()
    for (i in result.value.indices) result.value[i] = block(result.value[i])
    return result
}
