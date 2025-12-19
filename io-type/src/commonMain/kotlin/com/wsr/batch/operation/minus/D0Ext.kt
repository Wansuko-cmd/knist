package com.wsr.batch.operation.minus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.minus.minus

@JvmName("batchFloatMinusD0s")
operator fun Float.minus(other: Batch<IOType.D0>) = other.mapValue { this - it }

@JvmName("batchFloatMinusD1s")
operator fun Float.minus(other: Batch<IOType.D1>) = other.mapValue { this - it }

@JvmName("batchFloatMinusD2s")
operator fun Float.minus(other: Batch<IOType.D2>) = other.mapValue { this - it }

@JvmName("batchFloatMinusD3s")
operator fun Float.minus(other: Batch<IOType.D3>) = other.mapValue { this - it }

@JvmName("batchD0sMinusFloat")
operator fun Batch<IOType.D0>.minus(other: Float) = mapValue { it - other }

@JvmName("batchD0sMinusD0s")
operator fun Batch<IOType.D0>.minus(other: Batch<IOType.D0>) = Batch(size) { this[it] - other[it] }

@JvmName("batchD0sMinusD1s")
operator fun Batch<IOType.D0>.minus(other: Batch<IOType.D1>) = Batch(size) { this[it] - other[it] }

@JvmName("batchD0sMinusD2s")
operator fun Batch<IOType.D0>.minus(other: Batch<IOType.D2>) = Batch(size) { this[it] - other[it] }

@JvmName("batchD0sMinusD3s")
operator fun Batch<IOType.D0>.minus(other: Batch<IOType.D3>) = Batch(size) { this[it] - other[it] }
