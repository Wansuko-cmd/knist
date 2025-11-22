package com.wsr.batch.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get
import com.wsr.set

inline fun Batch<IOType.D3>.forEach(block: (IOType.D3) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D3>.map(block: (IOType.D3) -> IOType.D3): Batch<IOType.D3> = Batch(size) { block(this[it]) }

inline fun Batch<IOType.D3>.mapWith(
    other: Batch<IOType.D3>,
    block: (IOType.D3, IOType.D3) -> IOType.D3,
): Batch<IOType.D3> = Batch(size) { block(this[it], other[it]) }
