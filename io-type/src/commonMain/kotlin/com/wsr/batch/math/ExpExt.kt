package com.wsr.batch.math

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.core.IOType
import kotlin.math.exp

@JvmName("batchD1sExp")
fun Batch<IOType.D1>.exp(): Batch<IOType.D1> {
    val result = Backend.exp(x = value)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sExp")
fun Batch<IOType.D2>.exp(): Batch<IOType.D2> {
    val result = Backend.exp(x = value)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sExp")
fun Batch<IOType.D3>.exp(): Batch<IOType.D3> {
    val result = Backend.exp(x = value)
    return Batch(size = size, shape = shape, value = result)
}
