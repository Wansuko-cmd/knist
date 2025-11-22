package com.wsr.batch.div

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.div
import com.wsr.set

@JvmName("batchD1sDivD0s")
operator fun Batch<IOType.D1>.div(other: Float): Batch<IOType.D1> = map { it / other }

@JvmName("batchD1sDivD0s")
operator fun Batch<IOType.D1>.div(other: Batch<IOType.D0>): Batch<IOType.D1> = Batch(size) { this[it] / other[it] }

@JvmName("batchD1sDivD1")
operator fun Batch<IOType.D1>.div(other: IOType.D1) = map { it / other }

@JvmName("batchD1sDivD1s")
operator fun Batch<IOType.D1>.div(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a / b }
