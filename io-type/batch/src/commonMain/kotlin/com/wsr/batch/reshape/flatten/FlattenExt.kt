package com.wsr.batch.reshape.flatten

import com.wsr.batch.Batch
import com.wsr.core.IOType

@JvmName("batchD2sFlatten")
fun Batch<IOType.D2>.flatten() = Batch<IOType.D1>(
    shape = listOf(step),
    size = size,
    value = value,
)

@JvmName("batchD3sFlatten")
fun Batch<IOType.D3>.flatten() = Batch<IOType.D1>(
    shape = listOf(step),
    size = size,
    value = value,
)
