package com.wsr.batch.math

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.core.IOType

@JvmName("batchD0sSqrt")
fun Batch<IOType.D0>.sqrt(e: Float = 1e-7f): Batch<IOType.D0> {
    val result = Backend.sqrt(x = value, e = e)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD1sSqrt")
fun Batch<IOType.D1>.sqrt(e: Float = 1e-7f): Batch<IOType.D1> {
    val result = Backend.sqrt(x = value, e = e)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sSqrt")
fun Batch<IOType.D2>.sqrt(e: Float = 1e-7f): Batch<IOType.D2> {
    val result = Backend.sqrt(x = value, e = e)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sSqrt")
fun Batch<IOType.D3>.sqrt(e: Float = 1e-7f): Batch<IOType.D3> {
    val result = Backend.sqrt(x = value, e = e)
    return Batch(size = size, shape = shape, value = result)
}
