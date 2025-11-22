package com.wsr.batch.reshape

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.d2
import com.wsr.get
import com.wsr.reshape.broadcastToD2

fun Batch<IOType.D1>.broadcastToD2(axis: Int, size: Int) = Batch(this.size) { this[it].broadcastToD2(axis, size) }

fun Batch<IOType.D1>.toD2(): IOType.D2 = IOType.d2(listOf(size, shape[0]), value)

fun Batch<IOType.D1>.reshapeToD2(shape: List<Int>) = Batch<IOType.D2>(size = size, shape = shape, value = value)

fun Batch<IOType.D1>.reshapeToD3(shape: List<Int>) = Batch<IOType.D3>(size = size, shape = shape, value = value)
