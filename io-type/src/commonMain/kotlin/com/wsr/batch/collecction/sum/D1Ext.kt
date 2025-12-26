package com.wsr.batch.collecction.sum

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.sum.sum
import com.wsr.core.d0

fun Batch<IOType.D1>.sum(): Batch<IOType.D0> {
    val result = Backend.sum(x = value, xb = size)
    return Batch(shape = listOf(1), size = size, value = result)
}
