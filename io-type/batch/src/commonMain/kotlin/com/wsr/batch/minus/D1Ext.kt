package com.wsr.batch.minus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.operator.minus

@JvmName("floatMinusD1s")
operator fun Float.minus(other: Batch<IOType.D1>) = other.map { this - it }

@JvmName("batchD1sMinusD1")
operator fun Batch<IOType.D1>.minus(other: IOType.D1) = map { it - other }

@JvmName("batchD1sMinusD1s")
operator fun Batch<IOType.D1>.minus(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a - b }