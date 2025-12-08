package com.wsr.batch.reshape.convert

import com.wsr.batch.Batch
import com.wsr.core.IOType

fun Batch<IOType.D1>.toD2(): IOType.D2 = IOType.D2(shape = listOf(size, shape[0]), value = value)

fun IOType.D2.toD1(): Batch<IOType.D1> = Batch(value = value, size = shape[0], shape = listOf(shape[1]))

fun Batch<IOType.D2>.toD3(): IOType.D3 = IOType.D3(shape = listOf(size, shape[0], shape[1]), value = value)

fun IOType.D3.toBatch(): Batch<IOType.D2> = Batch(value = value, size = shape[0], shape = listOf(shape[1], shape[2]))

fun Batch<IOType.D3>.toD4(): IOType.D4 = IOType.D4(shape = listOf(size, shape[0], shape[1], shape[2]), value = value)

fun IOType.D4.toBatch(): Batch<IOType.D3> =
    Batch(value = value, size = shape[0], shape = listOf(shape[1], shape[2], shape[3]))
