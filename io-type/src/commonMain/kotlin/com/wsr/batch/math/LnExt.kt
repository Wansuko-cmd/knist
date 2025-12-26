package com.wsr.batch.math

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.core.IOType

@JvmName("batchD0sLn")
fun Batch<IOType.D0>.ln(e: Float = 1e-7f): Batch<IOType.D0> {
    val result = Backend.ln(x = value, e = e)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD1sLn")
fun Batch<IOType.D1>.ln(e: Float = 1e-7f): Batch<IOType.D1> {
    val result = Backend.ln(x = value, e = e)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sLn")
fun Batch<IOType.D2>.ln(e: Float = 1e-7f): Batch<IOType.D2> {
    val result = Backend.ln(x = value, e = e)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sLn")
fun Batch<IOType.D3>.ln(e: Float = 1e-7f): Batch<IOType.D3> {
    val result = Backend.ln(x = value, e = e)
    return Batch(size = size, shape = shape, value = result)
}
