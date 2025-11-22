package com.wsr.batch.func

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.mapValue

@JvmName("batchD1sSqrt")
fun Batch<IOType.D1>.sqrt(e: Float = 1e-7f): Batch<IOType.D1> = mapValue { kotlin.math.sqrt(it + e) }

@JvmName("batchD2sSqrt")
fun Batch<IOType.D2>.sqrt(e: Float = 1e-7f): Batch<IOType.D2> = mapValue { kotlin.math.sqrt(it + e) }

@JvmName("batchD3sSqrt")
fun Batch<IOType.D3>.sqrt(e: Float = 1e-7f): Batch<IOType.D3> = mapValue { kotlin.math.sqrt(it + e) }
