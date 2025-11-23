package com.wsr.batch.reshape

import com.wsr.batch.Batch
import com.wsr.core.IOType

fun Batch<IOType.D3>.flatten() = Batch<IOType.D1>(
    shape = listOf(step),
    size = size,
    value = value,
)

fun Batch<IOType.D3>.reshapeToD2(shape: List<Int>) = Batch<IOType.D2>(size = size, shape = shape, value = value)
