package com.wsr.batch.operation.minus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.minus.minus
import com.wsr.core.operation.minus.minus2

@JvmName("batchD2sMinusD0s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D0>): Batch<IOType.D2> = Batch(size) { this[it] - other[it] }

@JvmName("batchD2sMinusD1WithAxis")
fun Batch<IOType.D2>.minus2(other: IOType.D1, axis: Int) = map { it.minus2(other = other, axis = axis) }

@JvmName("batchD2sMinusD1sWithAxis")
fun Batch<IOType.D2>.minus2(other: Batch<IOType.D1>, axis: Int) =
    Batch(size) { this[it].minus2(other = other[it], axis = axis) }

@JvmName("batchD2sMinusD2")
operator fun Batch<IOType.D2>.minus(other: IOType.D2) = mapWith(other) { a, b -> a - b }

@JvmName("batchD2sMinusD2s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a - b }

@JvmName("batchD2sMinusD3s")
operator fun Batch<IOType.D2>.minus(other: Batch<IOType.D3>) = Batch(size) { this[it] - other[it] }

@JvmName("batchD2sMinusD3sWithAxisD")
fun Batch<IOType.D2>.minus(other: Batch<IOType.D3>, axis: Int) =
    Batch(size) { this[it].minus(other = other[it], axis = axis) }

@JvmName("batchD2sMinusD3sWithAxis")
fun Batch<IOType.D2>.minus2(other: Batch<IOType.D3>, axis1: Int, axis2: Int) =
    Batch(size) { this[it].minus2(other = other[it], axis1 = axis1, axis2 = axis2) }
