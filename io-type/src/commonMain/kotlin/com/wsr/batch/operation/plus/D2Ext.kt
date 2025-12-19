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

@JvmName("batchD2sPlusD1WithAxis")
fun Batch<IOType.D2>.plus(other: IOType.D1, axis: Int) = map { it.plus(other = other, axis = axis) }

@JvmName("batchD2sPlusD1sWithAxis")
fun Batch<IOType.D2>.plus(other: Batch<IOType.D1>, axis: Int) =
    Batch(size) { this[it].plus(other = other[it], axis = axis) }

@JvmName("batchD2sPlusD2")
operator fun Batch<IOType.D2>.plus(other: IOType.D2) = mapWith(other) { a, b -> a + b }

@JvmName("batchD2sPlusD2s")
operator fun Batch<IOType.D2>.plus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a + b }

@JvmName("batchD2sPlusD3sWithAxis")
fun Batch<IOType.D2>.plus(other: Batch<IOType.D3>, axis1: Int, axis2: Int) =
    Batch(size) { this[it].plus(other = other[it], axis1 = axis1, axis2 = axis2) }
