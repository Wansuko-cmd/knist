package com.wsr.batch.operation.times

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.operation.times.times
import com.wsr.core.operation.times.times

@JvmName("batchFloatTimesD2s")
operator fun Float.times(other: Batch<IOType.D2>) = other.map { this * it }

@JvmName("batchD2sTimesD0s")
operator fun Batch<IOType.D2>.times(other: Batch<IOType.D0>) = Batch(size) { this[it] * other[it].get() }

@JvmName("batchD2sMinusD1sWithAxis")
fun Batch<IOType.D2>.times(other: Batch<IOType.D1>, axis: Int) = Batch(size) { this[it].times(other = other[it], axis = axis) }

@JvmName("batchD2TimesD2s")
operator fun IOType.D2.times(other: Batch<IOType.D2>) = other.map { this * it }

@JvmName("batchD2sTimesD2")
operator fun Batch<IOType.D2>.times(other: IOType.D2) = map { it * other }

@JvmName("batchD2sTimesD2s")
operator fun Batch<IOType.D2>.times(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a * b }
