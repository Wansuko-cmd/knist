package com.wsr.batch.operation.plus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.plus.plus

@JvmName("batchD1sPlusD0s")
operator fun Batch<IOType.D1>.plus(other: Batch<IOType.D0>): Batch<IOType.D1> = Batch(size) { this[it] + other[it] }

@JvmName("batchD1sPlusD1")
operator fun Batch<IOType.D1>.plus(other: IOType.D1) = map { it + other }

@JvmName("batchD1sPlusD1s")
operator fun Batch<IOType.D1>.plus(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a + b }
