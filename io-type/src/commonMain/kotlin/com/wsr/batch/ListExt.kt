package com.wsr.batch

import com.wsr.base.DataBuffer
import com.wsr.core.IOType
import com.wsr.create

fun <T : IOType> List<T>.toBatch(): Batch<T> {
    val batchSize = size
    val shape = first().shape
    val step = shape.reduce { acc, i -> acc * i }
    val batchValue = DataBuffer.create(batchSize * step)
    forEachIndexed { index, item ->
        item.value.copyInto(batchValue, index * step)
    }
    return Batch(
        value = batchValue,
        size = batchSize,
        shape = shape,
    )
}

@JvmName("batchD1sToList")
fun Batch<IOType.D1>.toList(): List<IOType.D1> = List(size) { get(it) }

@JvmName("batchD2sToList")
fun Batch<IOType.D2>.toList(): List<IOType.D2> = List(size) { get(it) }

@JvmName("batchD3sToList")
fun Batch<IOType.D3>.toList(): List<IOType.D3> = List(size) { get(it) }
