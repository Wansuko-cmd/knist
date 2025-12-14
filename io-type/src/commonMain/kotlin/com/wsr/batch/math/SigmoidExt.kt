package com.wsr.batch.math

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.core.IOType
import kotlin.math.exp

@JvmName("batchD1sSigmoid")
fun Batch<IOType.D1>.sigmoid(): Batch<IOType.D1> = mapValue { 1 / (1 + exp(-it)) }

@JvmName("batchD2sSigmoid")
fun Batch<IOType.D2>.sigmoid(): Batch<IOType.D2> = mapValue { 1 / (1 + exp(-it)) }

@JvmName("batchD3sSigmoid")
fun Batch<IOType.D3>.sigmoid(): Batch<IOType.D3> = mapValue { 1 / (1 + exp(-it)) }
