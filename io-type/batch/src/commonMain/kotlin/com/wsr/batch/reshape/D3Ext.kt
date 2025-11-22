package com.wsr.batch.reshape

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.reshape.transpose

fun Batch<IOType.D2>.transpose() = map { it.transpose() }

fun Batch<IOType.D2>.flatten() = Batch<IOType.D1>(
    shape = listOf(step),
    size = size,
    value = value,
)