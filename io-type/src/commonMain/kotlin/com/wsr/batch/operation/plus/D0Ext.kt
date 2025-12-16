package com.wsr.batch.operation.plus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.plus.plus
import com.wsr.core.operation.times.times

@JvmName("batchFloatPlusD0s")
operator fun Float.plus(other: Batch<IOType.D0>) = Batch(other.size) { this + other[it] }

@JvmName("batchFloatPlusD1s")
operator fun Float.plus(other: Batch<IOType.D1>) = other.map { this + it }

@JvmName("batchFloatPlusD2s")
operator fun Float.plus(other: Batch<IOType.D2>) = other.map { this + it }

@JvmName("batchFloatPlusD3s")
operator fun Float.plus(other: Batch<IOType.D3>) = other.map { this + it }

@JvmName("batchD0sPlusFloat")
operator fun Batch<IOType.D0>.plus(other: Float) = Batch(size) { this[it] + other }

@JvmName("batchD0sPlusD0s")
operator fun Batch<IOType.D0>.plus(other: Batch<IOType.D0>) = Batch(size) { this[it] + other[it] }

@JvmName("batchD0sPlusD1s")
operator fun Batch<IOType.D0>.plus(other: Batch<IOType.D1>) = Batch(size) { this[it] + other[it] }

@JvmName("batchD0sPlusD2s")
operator fun Batch<IOType.D0>.plus(other: Batch<IOType.D2>) = Batch(size) { this[it] + other[it] }

@JvmName("batchD0sPlusD3s")
operator fun Batch<IOType.D0>.plus(other: Batch<IOType.D3>) = Batch(size) { this[it] + other[it] }
