package com.wsr.batch.operation.div

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div
import com.wsr.core.operation.div.div2

@JvmName("batchD2sDivFloat")
operator fun Batch<IOType.D2>.div(other: Float): Batch<IOType.D2> = map { it / other }

@JvmName("batchD2sDivD0s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D0>): Batch<IOType.D2> = Batch(size) { this[it] / other[it] }

@JvmName("batchD2sMinusD1")
operator fun Batch<IOType.D2>.div(other: IOType.D1) = map { it / other }

@JvmName("batchD2sMinusD1WithAxisD")
fun Batch<IOType.D2>.div(other: IOType.D1, axis: Int) = map { it.div(other = other, axis = axis) }

@JvmName("batchD2sMinusD1WithAxis")
fun Batch<IOType.D2>.div2(other: IOType.D1, axis: Int) = map { it.div2(other = other, axis = axis) }

@JvmName("batchD2sDivD1s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D1>) = Batch(size) { this[it] / other[it] }

@JvmName("batchD2sDivD1sWithAxisD")
fun Batch<IOType.D2>.div(other: Batch<IOType.D1>, axis: Int) =
    Batch(size) { this[it].div(other = other[it], axis = axis) }

@JvmName("batchD2sDivD1sWithAxis")
fun Batch<IOType.D2>.div2(other: Batch<IOType.D1>, axis: Int) =
    Batch(size) { this[it].div2(other = other[it], axis = axis) }

@JvmName("batchD2sDivD2")
operator fun Batch<IOType.D2>.div(other: IOType.D2) = mapWith(other) { a, b -> a / b }

@JvmName("batchD2sDivD2s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a / b }

@JvmName("batchD2sDivD3s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D3>) = Batch(size) { this[it] / other[it] }

@JvmName("batchD2sDivD3sWithAxis")
fun Batch<IOType.D2>.div(other: Batch<IOType.D3>, axis: Int) =
    Batch(size) { this[it].div(other = other[it], axis = axis) }
