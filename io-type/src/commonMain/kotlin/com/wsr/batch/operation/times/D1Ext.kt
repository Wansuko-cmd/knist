package com.wsr.batch.operation.times

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.operation.times.times
import com.wsr.core.operation.times.times2

@JvmName("batchD1sTimesD0s")
operator fun Batch<IOType.D1>.times(other: Batch<IOType.D0>) = Batch(size) { this[it] * other[it].get() }

@JvmName("batchD1TimesD1s")
operator fun IOType.D1.times(other: Batch<IOType.D1>) = other.map { this * it }

@JvmName("batchD1sTimesD1")
operator fun Batch<IOType.D1>.times(other: IOType.D1) = mapWith(other) { a, b -> a * b }

@JvmName("batchD1sTimesD1s")
operator fun Batch<IOType.D1>.times(other: Batch<IOType.D1>) = mapWith(other) { a, b -> a * b }

@JvmName("batchD1sTimesD2s")
operator fun Batch<IOType.D1>.times(other: Batch<IOType.D2>) = Batch(size) { this[it] * other[it] }

@JvmName("batchD1sTimesD2sWithAxisD")
fun Batch<IOType.D1>.times(other: Batch<IOType.D2>, axis: Int) =
    Batch(size) { this[it].times(other = other[it], axis = axis) }

@JvmName("batchD1sTimesD2sWithAxis")
fun Batch<IOType.D1>.times2(other: Batch<IOType.D2>, axis: Int) =
    Batch(size) { this[it].times2(other = other[it], axis = axis) }
