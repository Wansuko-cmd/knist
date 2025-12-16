package com.wsr.batch.operation.div

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div

@JvmName("batchFloatDivD0s")
operator fun Float.div(other: Batch<IOType.D0>) = Batch(other.size) { this / other[it] }

@JvmName("batchFloatDivD1s")
operator fun Float.div(other: Batch<IOType.D1>) = other.map { this / it }

@JvmName("batchFloatDivD2s")
operator fun Float.div(other: Batch<IOType.D2>) = other.map { this / it }

@JvmName("batchFloatDivD3s")
operator fun Float.div(other: Batch<IOType.D3>) = other.map { this / it }

@JvmName("batchD0sDivFloat")
operator fun Batch<IOType.D0>.div(other: Float) = Batch(size) { this[it] / other }

@JvmName("batchD0sDivD0s")
operator fun Batch<IOType.D0>.div(other: Batch<IOType.D0>) = Batch(size) { this[it] / other[it] }

@JvmName("batchD0sDivD1s")
operator fun Batch<IOType.D0>.div(other: Batch<IOType.D1>) = Batch(size) { this[it] / other[it] }

@JvmName("batchD0sDivD2s")
operator fun Batch<IOType.D0>.div(other: Batch<IOType.D2>) = Batch(size) { this[it] / other[it] }

@JvmName("batchD0sDivD3s")
operator fun Batch<IOType.D0>.div(other: Batch<IOType.D3>) = Batch(size) { this[it] / other[it] }
