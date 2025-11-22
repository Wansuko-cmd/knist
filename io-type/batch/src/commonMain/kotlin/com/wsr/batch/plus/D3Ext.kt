package com.wsr.batch.plus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.plus
import com.wsr.set

@JvmName("batchD3sPlusD0s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D0>): Batch<IOType.D3> = Batch(size) { this[it] + other[it] }

@JvmName("batchD3sPlusD3")
operator fun Batch<IOType.D3>.plus(other: IOType.D3) = map { it + other }

@JvmName("batchD3sPlusD3s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a + b }
