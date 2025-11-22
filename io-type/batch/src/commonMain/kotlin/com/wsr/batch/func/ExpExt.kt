package com.wsr.batch.func

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.mapValue
import kotlin.math.exp

@JvmName("batchD1sExp")
fun Batch<IOType.D1>.exp(): Batch<IOType.D1> = mapValue { exp(it) }

@JvmName("batchD2sExp")
fun Batch<IOType.D2>.exp(): Batch<IOType.D2> = mapValue { exp(it) }

@JvmName("batchD3sExp")
fun Batch<IOType.D3>.exp(): Batch<IOType.D3> = mapValue { exp(it) }
