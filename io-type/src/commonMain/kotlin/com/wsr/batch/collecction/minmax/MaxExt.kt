package com.wsr.batch.collecction.minmax

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.max.max
import com.wsr.core.d0

@JvmName("batchD1sMax")
fun Batch<IOType.D1>.max(): Batch<IOType.D0> {
    val result = Backend.max(x = value, xb = size)
    return Batch(shape = listOf(1), size = size, value = result)
}

@JvmName("batchD2sMax")
fun Batch<IOType.D2>.max(): Batch<IOType.D0> {
    val result = Backend.max(x = value, xb = size)
    return Batch(shape = listOf(1), size = size, value = result)
}

@JvmName("batchD3sMax")
fun Batch<IOType.D3>.max(): Batch<IOType.D0> {
    val result = Backend.max(x = value, xb = size)
    return Batch(shape = listOf(1), size = size, value = result)
}
