package com.wsr.batch.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get
import com.wsr.set

inline fun Batch<IOType.D2>.forEach(block: (IOType.D2) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D2>.map(block: (IOType.D2) -> IOType.D2): Batch<IOType.D2> {
    val result = copy()
    for (i in result.indices) {
        result[i] = block(result[i])
    }
    return result
}
