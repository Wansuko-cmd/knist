package com.wsr.batch.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get

inline fun Batch<IOType.D0>.forEach(block: (IOType.D0) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D0>.map(block: (IOType.D0) -> IOType.D0): Batch<IOType.D0> = Batch(size) { block(this[it]) }

inline fun Batch<IOType.D0>.mapWith(
    other: Batch<IOType.D0>,
    block: (IOType.D0, IOType.D0) -> IOType.D0,
): Batch<IOType.D0> = Batch(size) { block(this[it], other[it]) }
