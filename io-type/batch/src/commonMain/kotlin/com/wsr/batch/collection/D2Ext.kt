package com.wsr.batch.collection

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get
import com.wsr.set

inline fun Batch<IOType.D2>.forEach(block: (IOType.D2) -> Unit) {
    for (i in 0 until size) block(this[i])
}

inline fun Batch<IOType.D2>.map(block: (IOType.D2) -> IOType.D2): Batch<IOType.D2> {
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

inline fun Batch<IOType.D2>.mapWith(
    other: Batch<IOType.D2>,
    block: (IOType.D2, IOType.D2) -> IOType.D2,
): Batch<IOType.D2> {
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
