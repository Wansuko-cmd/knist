package com.wsr.batch.math

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.core.IOType

@JvmName("batchD0sSqrt")
fun Batch<IOType.D0>.sqrt(e: Float = 1e-7f): Batch<IOType.D0> = mapValue { kotlin.math.sqrt(it + e) }

@JvmName("batchD1sSqrt")
fun Batch<IOType.D1>.sqrt(e: Float = 1e-7f): Batch<IOType.D1> = mapValue { kotlin.math.sqrt(it + e) }

@JvmName("batchD2sSqrt")
fun Batch<IOType.D2>.sqrt(e: Float = 1e-7f): Batch<IOType.D2> = mapValue { kotlin.math.sqrt(it + e) }

@JvmName("batchD3sSqrt")
fun Batch<IOType.D3>.sqrt(e: Float = 1e-7f): Batch<IOType.D3> = mapValue { kotlin.math.sqrt(it + e) }
