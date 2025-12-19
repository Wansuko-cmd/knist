package com.wsr.batch.operation.minus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.minus.minus
import com.wsr.core.operation.minus.minus2

@JvmName("batchD1sMinusD0s")
operator fun Batch<IOType.D1>.minus(other: Batch<IOType.D0>): Batch<IOType.D1> = Batch(size) { this[it] - other[it] }

@JvmName("batchD1sMinusD1")
operator fun Batch<IOType.D1>.minus(other: IOType.D1) = mapWith(other) { a, b -> a - b }

@JvmName("batchD1sMinusD1s")
operator fun Batch<IOType.D1>.minus(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a - b }

@JvmName("batchD1sMinusD2sWithAxis")
fun Batch<IOType.D1>.minus2(other: Batch<IOType.D2>, axis: Int) =
    Batch(size) { this[it].minus2(other = other[it], axis = axis) }
