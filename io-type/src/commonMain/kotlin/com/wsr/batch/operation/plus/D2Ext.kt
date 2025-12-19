package com.wsr.batch.operation.plus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.plus.plus
import com.wsr.core.operation.plus.plus2

@JvmName("batchD2sPlusD0s")
operator fun Batch<IOType.D2>.plus(other: Batch<IOType.D0>): Batch<IOType.D2> = Batch(size) { this[it] + other[it] }

@JvmName("batchD2sPlusD1WithAxis")
fun Batch<IOType.D2>.plus2(other: IOType.D1, axis: Int) = map { it.plus2(other = other, axis = axis) }

@JvmName("batchD2sPlusD1sWithAxis")
fun Batch<IOType.D2>.plus2(other: Batch<IOType.D1>, axis: Int) =
    Batch(size) { this[it].plus2(other = other[it], axis = axis) }

@JvmName("batchD2sPlusD2")
operator fun Batch<IOType.D2>.plus(other: IOType.D2) = mapWith(other) { a, b -> a + b }

@JvmName("batchD2sPlusD2s")
operator fun Batch<IOType.D2>.plus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a + b }

@JvmName("batchD2sPlusD3s")
operator fun Batch<IOType.D2>.plus(other: Batch<IOType.D3>) = Batch(size) { this[it] + other[it] }

@JvmName("batchD2sPlusD3sWithAxis")
fun Batch<IOType.D2>.plus(other: Batch<IOType.D3>, axis: Int) =
    Batch(size) { this[it].plus(other = other[it], axis = axis) }
