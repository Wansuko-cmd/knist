package com.wsr.batch.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get
import com.wsr.set

inline fun Batch<IOType.D3>.forEach(block: (IOType.D3) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D3>.map(block: (IOType.D3) -> IOType.D3): Batch<IOType.D3> {
    val first = block(this[0])
    val value = FloatArray(first.value.size * size)
    first.value.copyInto(value, 0)
    for (i in 1 until size) {
        block(this[i]).value.copyInto(value, i * first.value.size)
    }
    return Batch(
        value = value,
        shape = first.shape,
        size = size,
    )
}

inline fun Batch<IOType.D3>.mapWith(
    other: Batch<IOType.D3>,
    block: (IOType.D3, IOType.D3) -> IOType.D3,
): Batch<IOType.D3> {
    val first = block(this[0], other[0])
    val value = FloatArray(first.value.size * size)
    first.value.copyInto(value, 0)
    for (i in 1 until size) {
        block(this[i], other[i]).value.copyInto(value, i * first.value.size)
    }
    return Batch(
        value = value,
        shape = first.shape,
        size = size,
    )
}
