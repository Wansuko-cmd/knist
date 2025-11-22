package com.wsr.batch.sum

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.collection.sum
import com.wsr.get

fun Batch<IOType.D2>.sum() = FloatArray(size) {
    val value = this[it].value
    var sum = 0f
    for (element in value) sum += element
    sum
}

fun Batch<IOType.D2>.sum(axis: Int) = Batch(size) { this[it].sum(axis) }
