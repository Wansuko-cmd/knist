package com.wsr.batch.reshape

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.reshape.broadcastToD2
import com.wsr.core.reshape.broadcastToD3
import com.wsr.core.reshape.slice
import com.wsr.core.reshape.transpose

fun Batch<IOType.D2>.transpose() = map { it.transpose() }

fun Batch<IOType.D2>.slice(i: IntRange = 0 until shape[0], j: IntRange = 0 until shape[1]) =
    map { it.slice(i = i, j = j) }

fun Batch<IOType.D2>.flatten() = Batch<IOType.D1>(
    shape = listOf(step),
    size = size,
    value = value,
)

fun Batch<IOType.D2>.reshapeToD3(shape: List<Int>) = Batch<IOType.D3>(size = size, shape = shape, value = value)

fun Batch<IOType.D2>.broadcastToD3(axis: Int, size: Int) = Batch(this.size) { this[it].broadcastToD3(axis, size) }
