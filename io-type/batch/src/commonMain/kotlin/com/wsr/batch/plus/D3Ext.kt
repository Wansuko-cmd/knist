package com.wsr.batch.plus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.plus
import com.wsr.set

@JvmName("batchD3sPlusFloats")
operator fun Batch<IOType.D3>.plus(other: FloatArray): Batch<IOType.D3> {
    val result = copy()
    for (i in result.indices) result[i] += other[i]
    return result
}

@JvmName("batchD3sPlusD3")
operator fun Batch<IOType.D3>.plus(other: IOType.D3) = map { it + other }

@JvmName("batchD3sPlusD3s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a + b }
