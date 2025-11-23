package com.wsr.batch.reshape

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.reshape.slice

fun Batch<IOType.D3>.slice(
    i: IntRange = 0 until shape[0],
    j: IntRange = 0 until shape[1],
    k: IntRange = 0 until shape[2],
) = map { it.slice(i = i, j = j, k = k) }

fun Batch<IOType.D3>.flatten() = Batch<IOType.D1>(
    shape = listOf(step),
    size = size,
    value = value,
)

fun Batch<IOType.D3>.reshapeToD2(shape: List<Int>) = Batch<IOType.D2>(size = size, shape = shape, value = value)
