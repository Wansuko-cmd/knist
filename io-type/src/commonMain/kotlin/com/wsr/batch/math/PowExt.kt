package com.wsr.batch.math

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType
import kotlin.math.pow

@JvmName("batchD0sPow")
fun Batch<IOType.D0>.pow(n: Int): Batch<IOType.D0> {
    val result = Backend.pow(x = value, n = n)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD1sPow")
fun Batch<IOType.D1>.pow(n: Int): Batch<IOType.D1> {
    val result = Backend.pow(x = value, n = n)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sPow")
fun Batch<IOType.D2>.pow(n: Int): Batch<IOType.D2> {
    val result = Backend.pow(x = value, n = n)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sPow")
fun Batch<IOType.D3>.pow(n: Int): Batch<IOType.D3> {
    val result = Backend.pow(x = value, n = n)
    return Batch(size = size, shape = shape, value = result)
}
