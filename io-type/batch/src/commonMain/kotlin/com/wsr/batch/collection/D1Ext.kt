package com.wsr.batch.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get

inline fun Batch<IOType.D1>.forEach(block: (IOType.D1) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D1>.map(block: (IOType.D1) -> IOType.D1): Batch<IOType.D1> =
    Batch(size) { block(this[it]) }

inline fun Batch<IOType.D1>.mapWith(
    other: Batch<IOType.D1>,
    block: (IOType.D1, IOType.D1) -> IOType.D1,
): Batch<IOType.D1> = Batch(size) { block(this[it], other[it]) }
