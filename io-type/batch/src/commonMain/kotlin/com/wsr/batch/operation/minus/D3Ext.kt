package com.wsr.batch.operation.minus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.minus.minus

@JvmName("batchFloatMinusD3s")
operator fun Float.minus(other: Batch<IOType.D3>) = other.map { this - it }

@JvmName("batchD3sMinusD0s")
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D0>): Batch<IOType.D3> = Batch(size) { this[it] - other[it] }

@JvmName("batchD3sMinusD3")
operator fun Batch<IOType.D3>.minus(other: IOType.D3) = map { it - other }

@JvmName("batchD3sMinusD3s")
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a - b }
