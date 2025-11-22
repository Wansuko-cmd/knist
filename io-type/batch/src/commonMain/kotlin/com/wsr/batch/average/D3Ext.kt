package com.wsr.batch.average

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.collection.average
import com.wsr.d0
import com.wsr.get
import com.wsr.operator.div
import com.wsr.operator.plus

@JvmName("batchD3sAverageBatch")
fun Batch<IOType.D3>.average(): Batch<IOType.D0> = Batch(size) { IOType.d0(this[it].average()) }

@JvmName("batchD2sAverageWithAxis")
fun Batch<IOType.D3>.average(axis: Int): Batch<IOType.D2> = Batch(size) { this[it].average(axis) }

@JvmName("batchD3sBatchAverage")
fun Batch<IOType.D3>.batchAverage(): IOType.D3 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
