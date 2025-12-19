package com.wsr.batch.collecction.map

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType

inline fun Batch<IOType.D1>.forEach(block: (IOType.D1) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D1>.map(block: (IOType.D1) -> IOType.D1): Batch<IOType.D1> = Batch(size) { block(this[it]) }
