package com.wsr.batch.times

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.operator.times

@JvmName("batchD2sTimesD2")
operator fun Batch<IOType.D2>.times(other: IOType.D2) = map { it * other }

@JvmName("batchD2sTimesD2s")
operator fun Batch<IOType.D2>.times(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a * b }
