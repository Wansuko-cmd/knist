package com.wsr.batch.func

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.mapValue
import kotlin.math.pow

@JvmName("batchD0sPow")
fun Batch<IOType.D0>.pow(n: Int): Batch<IOType.D0> = mapValue { it.pow(n) }

@JvmName("batchD1sPow")
fun Batch<IOType.D1>.pow(n: Int): Batch<IOType.D1> = mapValue { it.pow(n) }

@JvmName("batchD2sPow")
fun Batch<IOType.D2>.pow(n: Int): Batch<IOType.D2> = mapValue { it.pow(n) }

@JvmName("batchD3sPow")
fun Batch<IOType.D3>.pow(n: Int): Batch<IOType.D3> = mapValue { it.pow(n) }
