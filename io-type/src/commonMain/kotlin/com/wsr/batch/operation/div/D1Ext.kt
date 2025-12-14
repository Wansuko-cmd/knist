package com.wsr.batch.operation.div

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div

@JvmName("batchD1sDivD0s")
operator fun Batch<IOType.D1>.div(other: Float): Batch<IOType.D1> = map { it / other }

@JvmName("batchD1sDivD0s")
operator fun Batch<IOType.D1>.div(other: Batch<IOType.D0>): Batch<IOType.D1> = Batch(size) { this[it] / other[it] }

@JvmName("batchD1sDivD1")
operator fun Batch<IOType.D1>.div(other: IOType.D1) = map { it / other }

@JvmName("batchD1sDivD1s")
operator fun Batch<IOType.D1>.div(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a / b }

@JvmName("batchD1sDivD2s")
operator fun Batch<IOType.D1>.div(other: Batch<IOType.D2>) = Batch(size) { this[it] / other[it] }

@JvmName("batchD1sDivD2sWithAxis")
fun Batch<IOType.D1>.div(other: Batch<IOType.D2>, axis: Int) = Batch(size) { this[it].div(other = other[it], axis = axis) }
