package com.wsr.batch.operation.minus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.min.min
import com.wsr.core.operation.minus.minus

@JvmName("batchFloatMinusD2s")
operator fun Float.minus(other: Batch<IOType.D2>) = other.map { this - it }

@JvmName("batchD2sMinusD0s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D0>): Batch<IOType.D2> = Batch(size) { this[it] - other[it] }

@JvmName("batchD2sMinusD1s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D1>) = Batch(size) { this[it] - other[it] }

@JvmName("batchD2sMinusD1sWithAxis")
fun Batch<IOType.D2>.minus(other: Batch<IOType.D1>, axis: Int) = Batch(size) { this[it].minus(other = other[it], axis = axis) }

@JvmName("batchD2sMinusD2")
operator fun Batch<IOType.D2>.minus(other: IOType.D2) = map { it - other }

@JvmName("batchD2sMinusD2s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a - b }
