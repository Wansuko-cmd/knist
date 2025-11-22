package com.wsr.batch.div

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.div
import com.wsr.set

@JvmName("batchD2sDivFloats")
operator fun Batch<IOType.D2>.div(other: FloatArray): Batch<IOType.D2> {
    val result = copy()
    for (i in result.indices) result[i] /= other[i]
    return result
}

@JvmName("batchD2sDivD0s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D0>): Batch<IOType.D2> {
    val result = copy()
    for (i in result.indices) result[i] /= other[i].get()
    return result
}

@JvmName("batchD2sDivD1s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D1>) = Batch(size) { this[it] / other[it] }

@JvmName("batchD2sDivD2")
operator fun Batch<IOType.D2>.div(other: IOType.D2) = map { it / other }

@JvmName("batchD2sDivD2s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a / b }
