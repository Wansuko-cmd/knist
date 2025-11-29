package com.wsr.batch.reshape

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.reshape.broadcast.broadcastToD2
import com.wsr.core.reshape.slice.slice

fun Batch<IOType.D1>.broadcastToD2(axis: Int, size: Int) = Batch(this.size) { this[it].broadcastToD2(axis, size) }

fun Batch<IOType.D1>.toD2(): IOType.D2 = IOType.d2(listOf(size, shape[0]), value)

fun Batch<IOType.D1>.slice(i: IntRange) = map { it.slice(i = i) }

fun Batch<IOType.D1>.reshapeToD2(shape: List<Int>) = Batch<IOType.D2>(size = size, shape = shape, value = value)

fun Batch<IOType.D1>.reshapeToD3(shape: List<Int>) = Batch<IOType.D3>(size = size, shape = shape, value = value)
