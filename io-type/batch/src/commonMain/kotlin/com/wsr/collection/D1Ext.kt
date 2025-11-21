package com.wsr.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get
import com.wsr.set

inline fun Batch<IOType.D1>.forEach(block: (IOType.D1) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D1>.map(block: (IOType.D1) -> IOType.D1): Batch<IOType.D1> {
    val result = copy()
    for (i in result.indices) {
        result[i] = block(result[i])
    }
    return result
}
