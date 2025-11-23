package com.wsr.batch.collecction.map

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType

inline fun Batch<IOType.D3>.forEach(block: (IOType.D3) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D3>.map(block: (IOType.D3) -> IOType.D3): Batch<IOType.D3> = Batch(size) { block(this[it]) }

inline fun Batch<IOType.D3>.mapWith(
    other: Batch<IOType.D3>,
    block: (IOType.D3, IOType.D3) -> IOType.D3,
): Batch<IOType.D3> = Batch(size) { block(this[it], other[it]) }
