package com.wsr.batch.math

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.core.IOType

@JvmName("batchD0sLn")
fun Batch<IOType.D0>.ln(e: Float = 1e-7f): Batch<IOType.D0> = mapValue { kotlin.math.ln(it + e) }

@JvmName("batchD1sLn")
fun Batch<IOType.D1>.ln(e: Float = 1e-7f): Batch<IOType.D1> = mapValue { kotlin.math.ln(it + e) }

@JvmName("batchD2sLn")
fun Batch<IOType.D2>.ln(e: Float = 1e-7f): Batch<IOType.D2> = mapValue { kotlin.math.ln(it + e) }

@JvmName("batchD3sLn")
fun Batch<IOType.D3>.ln(e: Float = 1e-7f): Batch<IOType.D3> = mapValue { kotlin.math.ln(it + e) }
