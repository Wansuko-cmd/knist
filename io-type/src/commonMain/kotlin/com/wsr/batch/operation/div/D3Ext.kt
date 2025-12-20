package com.wsr.batch.operation.div

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div

@JvmName("batchD3sDivFloat")
operator fun Batch<IOType.D3>.div(other: Float): Batch<IOType.D3> = Batch(size) { this[it] / other }

@JvmName("batchD3sDivD0s")
operator fun Batch<IOType.D3>.div(other: Batch<IOType.D0>): Batch<IOType.D3> = Batch(size) { this[it] / other[it] }

@JvmName("batchD3sDivD2sWithAxis")
fun Batch<IOType.D2>.div(other: Batch<IOType.D3>, axis1: Int, axis2: Int) =
    Batch(size) { this[it].div(other = other[it], axis1 = axis1, axis2 = axis2) }

@JvmName("batchD3sDivD3")
operator fun Batch<IOType.D3>.div(other: IOType.D3) = mapWith(other) { a, b -> a / b }

@JvmName("batchD3sDivD3s")
operator fun Batch<IOType.D3>.div(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a / b }
