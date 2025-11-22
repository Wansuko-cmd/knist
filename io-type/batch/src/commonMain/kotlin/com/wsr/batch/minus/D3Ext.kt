package com.wsr.batch.minus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.minus
import com.wsr.set

@JvmName("batchFloatMinusD3s")
operator fun Float.minus(other: Batch<IOType.D3>) = other.map { this - it }

@JvmName("batchD3sMinusFloats")
operator fun Batch<IOType.D3>.minus(other: FloatArray): Batch<IOType.D3> {
    val result = copy()
    for (i in result.indices) result[i] -= other[i]
    return result
}

@JvmName("batchD3sMinusD3")
operator fun Batch<IOType.D3>.minus(other: IOType.D3) = map { it - other }

@JvmName("batchD3sMinusD3s")
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a - b }
