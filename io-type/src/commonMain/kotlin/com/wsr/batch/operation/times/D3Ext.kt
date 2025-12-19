package com.wsr.batch.operation.times

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.operation.times.times

@JvmName("batchD3sTimesD0s")
operator fun Batch<IOType.D3>.times(other: Batch<IOType.D0>) = Batch(size) { this[it] * other[it].get() }

@JvmName("batchD3sTimesD2sWithAxis")
fun Batch<IOType.D3>.times(other: Batch<IOType.D2>, axis1: Int, axis2: Int) =
    Batch(size) { this[it].times(other = other[it], axis1 = axis1, axis2 = axis2) }

@JvmName("batchD3TimesD3s")
operator fun IOType.D3.times(other: Batch<IOType.D3>) = other.map { this * it }

@JvmName("batchD3sTimesD3")
operator fun Batch<IOType.D3>.times(other: IOType.D3) = mapWith(other) { a, b -> a * b }

@JvmName("batchD3sTimesD3s")
operator fun Batch<IOType.D3>.times(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a * b }
