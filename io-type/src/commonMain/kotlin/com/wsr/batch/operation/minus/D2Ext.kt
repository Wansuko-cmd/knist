package com.wsr.batch.operation.minus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.minus.minus

@JvmName("batchD2sMinusD0s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D0>): Batch<IOType.D2> = Batch(size) { this[it] - other[it] }

@JvmName("batchD2sMinusD1")
operator fun Batch<IOType.D2>.minus(other: IOType.D1) = map { it - other }

@JvmName("batchD2sMinusD1WithAxis")
fun Batch<IOType.D2>.minus(other: IOType.D1, axis: Int) = map { it.minus(other = other, axis = axis) }

@JvmName("batchD2sMinusD1s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D1>) = Batch(size) { this[it] - other[it] }

@JvmName("batchD2sMinusD1sWithAxis")
fun Batch<IOType.D2>.minus(other: Batch<IOType.D1>, axis: Int) =
    Batch(size) { this[it].minus(other = other[it], axis = axis) }

@JvmName("batchD2sMinusD2")
operator fun Batch<IOType.D2>.minus(other: IOType.D2) = mapWith(other) { a, b -> a - b }

@JvmName("batchD2sMinusD2s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a - b }

@JvmName("batchD2sMinusD3s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D3>) = Batch(size) { this[it] - other[it] }

@JvmName("batchD2sMinusD3sWithAxis")
fun Batch<IOType.D2>.minus(other: Batch<IOType.D3>, axis: Int) =
    Batch(size) { this[it].minus(other = other[it], axis = axis) }
