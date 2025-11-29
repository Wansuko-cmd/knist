package com.wsr.batch.reshape.convert

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3

fun Batch<IOType.D1>.toD2(): IOType.D2 = IOType.d2(listOf(size, shape[0]), value)

fun IOType.D2.toD1(): Batch<IOType.D1> = Batch(value = value, size = shape[0], shape = listOf(shape[1]))

fun Batch<IOType.D2>.toD3(): IOType.D3 = IOType.d3(listOf(size, shape[0], shape[1]), value)

fun IOType.D3.toBatch(): Batch<IOType.D2> = Batch(value = value, size = shape[0], shape = listOf(shape[1], shape[2]))
