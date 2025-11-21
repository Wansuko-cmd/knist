package com.wsr.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get

inline fun Batch<IOType.D2>.forEach(block: (IOType.D2) -> Unit) {
    for (i in 0 until size) block(this[i])
}
