package com.wsr.batch.collecction.map

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType

inline fun Batch<IOType.D2>.forEach(block: (IOType.D2) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D2>.map(block: (IOType.D2) -> IOType.D2): Batch<IOType.D2> = Batch(size) { block(this[it]) }
