package com.wsr.batch.minus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.operator.minus

@JvmName("floatMinusD2s")
operator fun Float.minus(other: Batch<IOType.D2>) = other.map { this - it }

@JvmName("batchD2sMinusD2")
operator fun Batch<IOType.D2>.minus(other: IOType.D2) = map { it - other }

@JvmName("batchD2sMinusD2s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a - b }
