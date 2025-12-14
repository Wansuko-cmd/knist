package com.wsr.batch.operation.times

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.operation.times.times

@JvmName("batchFloatTimesD1s")
operator fun Float.times(other: Batch<IOType.D1>) = other.map { this * it }

@JvmName("batchD1sTimesD0s")
operator fun Batch<IOType.D1>.times(other: Batch<IOType.D0>) = Batch(size) { this[it] * other[it].get() }

@JvmName("batchD1TimesD1s")
operator fun IOType.D1.times(other: Batch<IOType.D1>) = other.map { this * it }

@JvmName("batchD1sTimesD1")
operator fun Batch<IOType.D1>.times(other: IOType.D1) = map { it * other }

@JvmName("batchD1sTimesD1s")
operator fun Batch<IOType.D1>.times(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a * b }
