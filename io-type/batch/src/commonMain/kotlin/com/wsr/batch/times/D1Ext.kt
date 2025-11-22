package com.wsr.batch.times

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.operator.minus
import com.wsr.operator.times

@JvmName("batchFloatTimesD1s")
operator fun Float.times(other: Batch<IOType.D1>) = other.map { this * it }

@JvmName("batchD1TimesD1s")
operator fun IOType.D1.times(other: Batch<IOType.D1>) = other.map { this * it }

@JvmName("batchD1sTimesD1")
operator fun Batch<IOType.D1>.times(other: IOType.D1) = map { it * other }

@JvmName("batchD1sTimesD1s")
operator fun Batch<IOType.D1>.times(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a * b }
