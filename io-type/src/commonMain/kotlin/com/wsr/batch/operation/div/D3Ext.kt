package com.wsr.batch.operation.div

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div

@JvmName("batchD3sDivFloat")
operator fun Batch<IOType.D3>.div(other: Float): Batch<IOType.D3> = Batch(size) { this[it] / other }

@JvmName("batchD3sDivD0s")
operator fun Batch<IOType.D3>.div(other: Batch<IOType.D0>): Batch<IOType.D3> = Batch(size) { this[it] / other[it] }

@JvmName("batchD3sMinusD2")
operator fun Batch<IOType.D3>.div(other: IOType.D2) = map { it / other }

@JvmName("batchD3sMinusD2WithAxis")
fun Batch<IOType.D3>.div(other: IOType.D2, axis: Int) = map { it.div(other = other, axis = axis) }

@JvmName("batchD3sMinusD2s")
operator fun Batch<IOType.D3>.div(other: Batch<IOType.D2>) = Batch(size) { this[it] / other[it] }

@JvmName("batchD3sMinusD2sWithAxis")
fun Batch<IOType.D3>.div(other: Batch<IOType.D2>, axis: Int) =
    Batch(size) { this[it].div(other = other[it], axis = axis) }

@JvmName("batchD3sDivD3")
operator fun Batch<IOType.D3>.div(other: IOType.D3) = mapWith(other) { a, b -> a / b }

@JvmName("batchD3sDivD3s")
operator fun Batch<IOType.D3>.div(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a / b }
