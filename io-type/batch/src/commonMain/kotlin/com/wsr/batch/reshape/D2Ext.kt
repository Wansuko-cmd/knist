package com.wsr.batch.reshape

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.reshape.transpose

fun Batch<IOType.D2>.transpose() = map { it.transpose() }

fun Batch<IOType.D2>.flatten() = Batch<IOType.D1>(
    shape = listOf(step),
    size = size,
    value = value,
)

fun Batch<IOType.D2>.reshapeToD3(shape: List<Int>) = Batch<IOType.D3>(size = size, shape = shape, value = value)
