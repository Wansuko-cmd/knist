package com.wsr.batch.func

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.mapValue
import kotlin.math.exp

@JvmName("batchD1sSigmoid")
fun Batch<IOType.D1>.sigmoid(): Batch<IOType.D1> = mapValue { 1 / (1 + exp(-it)) }

@JvmName("batchD2sSigmoid")
fun Batch<IOType.D2>.sigmoid(): Batch<IOType.D2> = mapValue { 1 / (1 + exp(-it)) }

@JvmName("batchD3sSigmoid")
fun Batch<IOType.D3>.sigmoid(): Batch<IOType.D3> = mapValue { 1 / (1 + exp(-it)) }
