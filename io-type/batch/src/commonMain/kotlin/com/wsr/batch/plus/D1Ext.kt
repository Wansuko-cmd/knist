package com.wsr.batch.plus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.plus
import com.wsr.set

@JvmName("batchD1sPlusD0s")
operator fun Batch<IOType.D1>.plus(other: Batch<IOType.D0>): Batch<IOType.D1> {
    val result = copy()
    for (i in result.indices) result[i] += other[i].get()
    return result
}

@JvmName("batchD1sPlusD1")
operator fun Batch<IOType.D1>.plus(other: IOType.D1) = map { it + other }

@JvmName("batchD1sPlusD1s")
operator fun Batch<IOType.D1>.plus(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a + b }
