package com.wsr.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get

inline fun Batch<IOType.D1>.forEach(block: (IOType.D1) -> Unit) {
    for (i in 0 until size) block(this[i])
}
