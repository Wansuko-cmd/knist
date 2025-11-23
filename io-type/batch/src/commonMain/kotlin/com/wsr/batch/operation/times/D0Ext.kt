package com.wsr.batch.operation.times

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.times

@JvmName("batchD0sTimesD0s")
operator fun Batch<IOType.D0>.times(other: Batch<IOType.D0>) = Batch(size) { this[it] * other[it] }

@JvmName("batchD0sTimesD1s")
operator fun Batch<IOType.D0>.times(other: Batch<IOType.D1>) = Batch(size) { this[it] * other[it] }

@JvmName("batchD0sTimesD2s")
operator fun Batch<IOType.D0>.times(other: Batch<IOType.D2>) = Batch(size) { this[it] * other[it] }

@JvmName("batchD0sTimesD3s")
operator fun Batch<IOType.D0>.times(other: Batch<IOType.D3>) = Batch(size) { this[it] * other[it] }
