package com.wsr.batch.minus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.operator.minus

@JvmName("floatMinusD3s")
operator fun Float.minus(other: Batch<IOType.D3>) = other.map { this - it }

@JvmName("batchD3sMinusD3")
operator fun Batch<IOType.D3>.minus(other: IOType.D3) = map { it - other }

@JvmName("batchD3sMinusD3s")
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a - b }
