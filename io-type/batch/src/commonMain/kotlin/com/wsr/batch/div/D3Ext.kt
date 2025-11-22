package com.wsr.batch.div

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.div
import com.wsr.set

@JvmName("batchD3sDivFloats")
operator fun Batch<IOType.D3>.div(other: FloatArray): Batch<IOType.D3> {
    val result = copy()
    for (i in result.indices) result[i] /= other[i]
    return result
}

@JvmName("batchD3sDivD3")
operator fun Batch<IOType.D3>.div(other: IOType.D3) = map { it / other }

@JvmName("batchD3sDivD3s")
operator fun Batch<IOType.D3>.div(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a / b }
