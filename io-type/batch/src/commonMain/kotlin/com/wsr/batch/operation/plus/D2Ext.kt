package com.wsr.batch.operation.plus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.plus.plus
import com.wsr.core.operation.plus.plus

@JvmName("batchD2sPlusD0s")
operator fun Batch<IOType.D2>.plus(other: Batch<IOType.D0>): Batch<IOType.D2> = Batch(size) { this[it] + other[it] }

@JvmName("batchD2sMinusD1sWithAxis")
fun Batch<IOType.D2>.plus(other: Batch<IOType.D1>, axis: Int) = Batch(size) { this[it].plus(other = other[it], axis = axis) }

@JvmName("batchD2sPlusD2")
operator fun Batch<IOType.D2>.plus(other: IOType.D2) = map { it + other }

@JvmName("batchD2sPlusD2s")
operator fun Batch<IOType.D2>.plus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a + b }
