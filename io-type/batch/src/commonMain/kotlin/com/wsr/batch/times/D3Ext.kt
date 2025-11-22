package com.wsr.batch.times

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.times

@JvmName("batchFloatTimesD3s")
operator fun Float.times(other: Batch<IOType.D3>) = other.map { this * it }

@JvmName("batchD3sTimesD0s")
operator fun Batch<IOType.D3>.times(other: Batch<IOType.D0>) = Batch(size) { this[it] * other[it].get() }

@JvmName("batchD3TimesD3s")
operator fun IOType.D3.times(other: Batch<IOType.D3>) = other.map { this * it }

@JvmName("batchD3sTimesD3")
operator fun Batch<IOType.D3>.times(other: IOType.D3) = map { it * other }

@JvmName("batchD3sTimesD3s")
operator fun Batch<IOType.D3>.times(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a * b }
