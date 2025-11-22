package com.wsr.batch.plus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.plus
import com.wsr.set

@JvmName("batchD2sPlusD0s")
operator fun Batch<IOType.D2>.plus(other: Batch<IOType.D0>): Batch<IOType.D2> = Batch(size) { this[it] + other[it] }

@JvmName("batchD2sPlusD2")
operator fun Batch<IOType.D2>.plus(other: IOType.D2) = map { it + other }

@JvmName("batchD2sPlusD2s")
operator fun Batch<IOType.D2>.plus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a + b }
