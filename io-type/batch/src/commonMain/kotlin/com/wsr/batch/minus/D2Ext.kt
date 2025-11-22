package com.wsr.batch.minus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.minus
import com.wsr.set

@JvmName("batchFloatMinusD2s")
operator fun Float.minus(other: Batch<IOType.D2>) = other.map { this - it }

@JvmName("batchD2sMinusFloats")
operator fun Batch<IOType.D2>.minus(other: FloatArray): Batch<IOType.D2> {
    val result = copy()
    for (i in result.indices) result[i] -= other[i]
    return result
}

@JvmName("batchD2sMinusD1s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D1>) = Batch(size) { this[it] - other[it] }

@JvmName("batchD2sMinusD2")
operator fun Batch<IOType.D2>.minus(other: IOType.D2) = map { it - other }

@JvmName("batchD2sMinusD2s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a - b }
