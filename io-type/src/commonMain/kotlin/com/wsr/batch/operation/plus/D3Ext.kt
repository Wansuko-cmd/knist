package com.wsr.batch.operation.plus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.plus.plus

@JvmName("batchD3sPlusD0s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D0>): Batch<IOType.D3> = Batch(size) { this[it] + other[it] }

@JvmName("batchD3sMinusD2")
operator fun Batch<IOType.D3>.plus(other: IOType.D2) = map { it.plus(other, axis1 = 1, axis2 = 2) }

@JvmName("batchD3sPlusD2s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D2>) = plus(other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sPlusD2sWithAxis")
fun Batch<IOType.D3>.plus(other: Batch<IOType.D2>, axis1: Int, axis2: Int) =
    Batch(size) { this[it].plus(other = other[it], axis1 = axis1, axis2 = axis2) }

@JvmName("batchD3sPlusD3")
operator fun Batch<IOType.D3>.plus(other: IOType.D3) = mapWith(other) { a, b -> a + b }

@JvmName("batchD3sPlusD3s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a + b }
